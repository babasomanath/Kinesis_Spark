/**
 * 
 */
package com.sample.kinesis.driver;

import java.util.Arrays;
import java.util.Scanner;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudsearchv2.model.ResourceNotFoundException;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.model.CreateStreamRequest;
import com.amazonaws.services.kinesis.model.DescribeStreamRequest;
import com.amazonaws.services.kinesis.model.DescribeStreamResult;

/**
 * @author somanatn
 *
 */
public class CreateKinesisStream {

	private static final String STREAM_NAME = "use_Case_Kinesis_Stream";
	private static final int SHARD_COUNT = 4;
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
	public static void main(String[] args) {
		System.out.println("===========================================");
		System.out.println("Welcome To Amazon Kinesis!");
		System.out.println("===========================================");
		System.out.println("=======Select A Region, from the list =======");
		System.out.println(Arrays.asList(Regions.values()));
		System.out.println("Enter the Region Name : ");
		Scanner keyboard = new Scanner(System.in);
		String regionName = keyboard.nextLine();
		String formattedRegionName = regionName.toUpperCase();
		try {
			Regions region = Regions.valueOf(formattedRegionName);
			String endpoint = "kinesis.".concat(formattedRegionName.toLowerCase().replace('_', '-')).concat(".amazonaws.com") ;
			System.out.println("Endpoint :   "+endpoint);
			init(region,endpoint);
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
			    //
			    // sleep for one second
			    //
			    try {
			      Thread.sleep( 1000 );
			    }
			    catch ( Exception e ) {}
			  }
			  catch ( ResourceNotFoundException e ) {}
			}
			if ( System.currentTimeMillis() >= endTime ) {
			  throw new RuntimeException("Stream " + STREAM_NAME + " never went active" );
			}else{
				System.out.println(STREAM_NAME+"   : Stream is Active ..");
			}
		} catch (IllegalArgumentException iae) {
			System.out.println("Invalid region Name ..");
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
