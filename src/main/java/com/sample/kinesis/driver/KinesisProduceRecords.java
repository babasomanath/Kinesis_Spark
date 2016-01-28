/**
 * 
 */
package com.sample.kinesis.driver;

import java.nio.ByteBuffer;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudsearchv2.model.ResourceNotFoundException;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.model.CreateStreamRequest;
import com.amazonaws.services.kinesis.model.DescribeStreamRequest;
import com.amazonaws.services.kinesis.model.DescribeStreamResult;
import com.amazonaws.services.kinesis.producer.KinesisProducerConfiguration;
import com.amazonaws.services.kinesis.producer.KinesisProducer;
/**
 * @author Somanath Nanda
 *
 */
public class KinesisProduceRecords {

	private static final String STREAM_NAME = "New_Stream_Test_Prodcuer";
	private static final int SHARD_COUNT = 4;
	private static final String REGION_NAME = "EU_WEST_1";
	static AmazonKinesisClient kinesisClient;

	private static void init(Regions regionForKinesisStream,String endpoint) throws Exception {
		AWSCredentials credentials = null;
		Region region = Region.getRegion(regionForKinesisStream);
		try {
			credentials = new ProfileCredentialsProvider().getCredentials();

		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. "
							+ "Please make sure that your credentials file is at the correct "
							+ "location (~/.aws/credentials), and is in valid format.",
							e);
		}
		kinesisClient = new AmazonKinesisClient().withRegion(region).withEndpoint(endpoint);
		kinesisClient.setServiceNameIntern("kinesis");
	}
	private static void createStream() throws Exception{
		CreateStreamRequest streamRequest = new CreateStreamRequest();
		streamRequest.setStreamName(STREAM_NAME);
		streamRequest.setShardCount(SHARD_COUNT);
		kinesisClient.createStream(streamRequest);
		DescribeStreamRequest describeStream = new DescribeStreamRequest();
		describeStream.setStreamName(STREAM_NAME);
		long startTime = System.currentTimeMillis();
		long endTime = startTime + ( 10 * 60 * 1000 );
		while ( System.currentTimeMillis() < endTime ) {
		  try {
		    Thread.sleep(20 * 1000);
		  } 
		  catch ( Exception e ) {}
		  
		  try {
		    DescribeStreamResult describeStreamResponse = kinesisClient.describeStream(describeStream );
		    String streamStatus = describeStreamResponse.getStreamDescription().getStreamStatus();
		    if ( streamStatus.equals( "ACTIVE" ) ) {
		      break;
		    }
		    // sleep for one second
		    try {
		      Thread.sleep( 1000 );
		    }
		    catch ( Exception e ) {throw e;}
		  }
		  catch ( ResourceNotFoundException rnfe ) {throw rnfe;}
		}
		if ( System.currentTimeMillis() >= endTime ) {
		  throw new RuntimeException("Stream " + STREAM_NAME + " never went active" );
		}else{
			System.out.println(STREAM_NAME+"   : Stream is Active ..");
		}
	}
	private static void describeStream() throws Exception{
		System.out.println("===========================================");
		System.out.println("Welcome To Amazon Kinesis!");
		System.out.println("===========================================");
		try {
			int numShards = kinesisClient.describeStream(STREAM_NAME).getStreamDescription().getShards().size();
			int numOfStreams = numShards;
			System.out.println("************** -- Stream Name       " + STREAM_NAME);
			System.out.println("************** -- numShards         " + numShards);
			System.out.println("************** -- number of streams " + numOfStreams);
		}catch (AmazonServiceException ase) {
			throw ase;
		} catch (Exception e) {
			throw e;
		}
	}
	private static KinesisProducerConfiguration getKinesisProducerConfiguration(Regions regionForKinesisStream,String endpoint) throws Exception{
		KinesisProducerConfiguration kinesisProducerConfiguration = new KinesisProducerConfiguration();
		Region region = Region.getRegion(regionForKinesisStream);
		AWSCredentialsProvider awsCredentialsProvider = null;
		try {
			awsCredentialsProvider = new AWSCredentialsProvider() {
				@Override
				public void refresh() {
					System.out.println(" ===  Refreshing  ==== ");
				}
				@Override
				public AWSCredentials getCredentials() {
					return new ProfileCredentialsProvider().getCredentials();
				}
			};
			kinesisProducerConfiguration.setCredentialsProvider(awsCredentialsProvider);
			kinesisProducerConfiguration.setRegion(region.getName());
			kinesisProducerConfiguration.setCustomEndpoint(endpoint);
			kinesisProducerConfiguration.setMaxConnections(1);
		}catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. "
							+ "Please make sure that your credentials file is at the correct "
							+ "location (~/.aws/credentials), and is in valid format.",
							e);
		}
		return kinesisProducerConfiguration;
	}
	private static void produceRecords(Regions regionForKinesisStream,String endpoint) throws Exception{
		try{
			KinesisProducerConfiguration config = getKinesisProducerConfiguration(regionForKinesisStream, endpoint);
			KinesisProducer kinesisProducer = new KinesisProducer(config);
			for (int i = 300; i < 400; i++) {
				kinesisProducer.addUserRecord(STREAM_NAME, String.format("partitionKey-%d", i), ByteBuffer.wrap(String.valueOf("UseCase- (.Y.)"+i).getBytes()));
			}
			System.out.println("Successfully added all the records....  ");
		}catch(AmazonServiceException ase){
			throw ase;
		}catch(Exception e){
			throw e;
		}
	}
	public static void main(String[] args) {
		try {
			Regions region = Regions.valueOf(REGION_NAME);
			String endpoint = "kinesis.".concat(REGION_NAME.toLowerCase().replace('_', '-')).concat(".amazonaws.com") ;
			System.out.println("Endpoint :   "+endpoint);
			init(region,endpoint);
			//createStream();
			describeStream();
			produceRecords(region, endpoint);
		}catch (AmazonServiceException ase) {
			System.out.println("Caught Exception: " + ase.getMessage());
			System.out.println("Reponse Status Code: " + ase.getStatusCode());
			System.out.println("Error: " + ase.getErrorCode());
			System.out.println("Request" + ase.getRequestId());
		} catch (Exception e) {
			System.out.println("Exception Thrown  "+e);
			e.printStackTrace();
		}
	}

}
