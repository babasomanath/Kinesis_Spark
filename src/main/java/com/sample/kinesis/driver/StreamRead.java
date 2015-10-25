/**
 * 
 */
package com.sample.kinesis.driver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.model.DescribeStreamRequest;
import com.amazonaws.services.kinesis.model.DescribeStreamResult;
import com.amazonaws.services.kinesis.model.GetRecordsRequest;
import com.amazonaws.services.kinesis.model.GetRecordsResult;
import com.amazonaws.services.kinesis.model.GetShardIteratorRequest;
import com.amazonaws.services.kinesis.model.GetShardIteratorResult;
import com.amazonaws.services.kinesis.model.Record;
import com.amazonaws.services.kinesis.model.Shard;

/**
 * @author somanatn
 *
 */
public class StreamRead {

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
			String streamARN = kinesisClient.describeStream(STREAM_NAME).getStreamDescription().getStreamARN();
			
			String shardIterator = null;
			String shardIteratorType = "TRIM_HORIZON";
			GetShardIteratorRequest getShardIteratorRequest = new GetShardIteratorRequest().withStreamName(STREAM_NAME).withShardIteratorType(shardIteratorType);
			
			DescribeStreamRequest describeStreamRequest = new DescribeStreamRequest().withStreamName(STREAM_NAME);
			List<Shard> shards = new ArrayList<>();
			String exclusiveStartShardId = null;
			
			System.out.println("************** -- Stream Name       " + STREAM_NAME);
			System.out.println("************** -- Stream ARN        " + streamARN);
			System.out.println("************** -- numShards         " + numShards);
			System.out.println("************** -- number of streams " + numOfStreams);
			
			do {
			    describeStreamRequest.setExclusiveStartShardId( exclusiveStartShardId );
			    DescribeStreamResult describeStreamResult = kinesisClient.describeStream( describeStreamRequest );
			    shards.addAll( describeStreamResult.getStreamDescription().getShards() );
			    if(shards.size()>0){
			    	for (Shard shard : shards) {
			    		System.out.println("Shard ID : "+shard.getShardId());
			    		System.out.println("  Starting Hash-Key : "+shard.getHashKeyRange().getStartingHashKey());
			    		getShardIteratorRequest.setShardId(shard.getShardId());
			    		GetShardIteratorResult getShardIteratorResult =kinesisClient.getShardIterator(getShardIteratorRequest);
			    		shardIterator = getShardIteratorResult.getShardIterator();
			    		GetRecordsRequest getRecordsRequest = new GetRecordsRequest();
			    		getRecordsRequest.setShardIterator(shardIterator);
			    		getRecordsRequest.setLimit(25);

			    		GetRecordsResult getRecordsResult = kinesisClient.getRecords(getRecordsRequest);
			    		List<Record> records = getRecordsResult.getRecords();
			    		if(records!=null && records.size()>0){
			    			for (Record record : records) {
			    				System.out.println("------------------------------------------------------------------");
								System.out.println("    Partition Key   :  "+record.getPartitionKey());
								System.out.println("    Data            :  "+record.getData().get());
								System.out.println("    Sequence Number :  "+record.getSequenceNumber());
								System.out.println("------------------------------------------------------------------");
							}
			    		}
			    		System.out.println("  Ending Hash-Key   : "+shard.getHashKeyRange().getEndingHashKey());
			    	}
			    }
			    if (describeStreamResult.getStreamDescription().getHasMoreShards() && shards.size() > 0) {
			    	System.out.println("There are additional shards ..");
			        exclusiveStartShardId = shards.get(shards.size() - 1).getShardId();
			    } else {
			    	System.out.println("There are no additional shards ..");
			        exclusiveStartShardId = null;
			    }
			} while ( exclusiveStartShardId != null );	
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
