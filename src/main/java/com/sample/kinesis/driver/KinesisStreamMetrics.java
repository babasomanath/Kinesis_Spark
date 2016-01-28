package com.sample.kinesis.driver;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;

/**
 * @author Somanath Nanda
 *
 */
public class KinesisStreamMetrics {
	public static void main(String[] args) {
		AmazonCloudWatchClient acwc = new AmazonCloudWatchClient(new ProfileCredentialsProvider())
											.withRegion(Regions.valueOf("EU_WEST_1"));
		GetMetricStatisticsRequest gmsr = new GetMetricStatisticsRequest();
		
	}
}
