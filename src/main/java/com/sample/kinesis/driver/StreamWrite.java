/**
 * 
 */
package com.sample.kinesis.driver;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.spark.streaming.Duration;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.model.PutRecordsRequest;
import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry;
import com.amazonaws.services.kinesis.model.PutRecordsResult;

/**
 * @author somanatn
 *
 */
public class StreamWrite {

	private static final String STREAM_NAME = "use_Case_Kinesis_Stream";
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
			int numShards = kinesisClient.describeStream(STREAM_NAME).getStreamDescription().getShards().size();
			int numOfStreams = numShards;
			System.out.println("************** -- Stream Name       " + STREAM_NAME);
			System.out.println("************** -- numShards         " + numShards);
			System.out.println("************** -- number of streams " + numOfStreams);
			// Spark Streaming batch interval

			/*Duration batchInterval = new Duration(9000);

			System.out.println("************** -- batchInterval     " + batchInterval.prettyPrint());
	
			// Kinesis checkpoint interval. Same as batchInterval 

			Duration kinesisCheckpointInterval = batchInterval;*/
			
			PutRecordsRequest putRecordsRequest  = new PutRecordsRequest();
			putRecordsRequest.setStreamName(STREAM_NAME);
			List <PutRecordsRequestEntry> putRecordsRequestEntryList  = new ArrayList<>(); 
			for (int i = 0; i < 100; i++) {
			    PutRecordsRequestEntry putRecordsRequestEntry  = new PutRecordsRequestEntry();
			    putRecordsRequestEntry.setData(ByteBuffer.wrap(String.valueOf(i).getBytes()));
			    putRecordsRequestEntry.setPartitionKey(String.format("partitionKey-%d", i));
			    putRecordsRequestEntryList.add(putRecordsRequestEntry); 
			}

			putRecordsRequest.setRecords(putRecordsRequestEntryList);
			PutRecordsResult putRecordsResult  = kinesisClient.putRecords(putRecordsRequest);
			System.out.println("Put Result" + putRecordsResult);
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
