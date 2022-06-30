package com.batch.demo.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;

import com.batch.demo.entity.Product;
import com.batch.demo.listener.MyJobListener;
import com.batch.demo.processor.ProductProcessor;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	// a. Reader class object
	@Bean
	public FlatFileItemReader<Product> reader() {
		FlatFileItemReader<Product> reader = new FlatFileItemReader<>();
		reader.setResource(new ClassPathResource("products.csv"));
		// reader.setResource(new FileSystemResource(""));
		// reader.setResource(new UrlResource(""));

		reader.setLineMapper(new DefaultLineMapper<Product>() {{
			setLineTokenizer(new DelimitedLineTokenizer() {{
				setDelimiter(DELIMITER_COMMA);
				setNames("prodId","prodCode","prodCost");
			}});
			setFieldSetMapper(new BeanWrapperFieldSetMapper<Product>() {
	               {
	                  setTargetType(Product.class);
	               }
	            });
		}});
		
		return reader;
	}
	/*@Bean
	   public FlatFileItemReader<Product> reader() {
	      FlatFileItemReader<Product> reader = new FlatFileItemReader<Product>();
	      reader.setResource(new ClassPathResource("products.csv"));
	      reader.setLineMapper(new DefaultLineMapper<Product>() {
	         {
	            setLineTokenizer(new DelimitedLineTokenizer() {
	               {
	                  setNames(new String[] { "firstName", "lastName" });
	               }
	            });
	            setFieldSetMapper(new BeanWrapperFieldSetMapper<Product>() {
	               {
	                  s etTargetType(Product.class);
	               }
	            });
	         }
	      });
	      return reader;
	   }*/
	// b. Processor class object
	@Bean
	public ItemProcessor<Product, Product> processor() {
		
		
		return new ProductProcessor();
	}

	@Autowired
	private DataSource dataSource;
	
	// c. Writer class object
	@Bean
	@Primary
	public JdbcBatchItemWriter<Product> writer() {
		JdbcBatchItemWriter<Product> writer = new JdbcBatchItemWriter<>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Product>());
		writer.setSql("INSERT INTO PRODUCTS(PID,PCODE,PCOST,PDISC,PGST) VALUES (:prodId,:prodCode,:prodCost,:prodDisc,:prodGst)");
		writer.setDataSource(dataSource);
		return writer;
	}

	// d. Listener class object
	@Bean
	public JobExecutionListener listener() {
		return new MyJobListener();
	}

	// e. Autowire Step builder Factory
	@Autowired
	private StepBuilderFactory sf;

	// f. Step object
	@Bean
	public Step stepA() {
		return sf.get("stepA")
			  .<Product, Product>chunk(2)
			  .reader(reader())
			  .processor(processor())
			  .writer(writer())
			  .build();
				
	}

	// g. Autowire Job builder Factory
	@Autowired
	private JobBuilderFactory jf;

	// d. Job object
	@Bean
	public Job jobA() {
		return jf.get("jobA")
				.incrementer(new RunIdIncrementer())
				.listener(listener())
				.start(stepA())
				.build();
	}
}
