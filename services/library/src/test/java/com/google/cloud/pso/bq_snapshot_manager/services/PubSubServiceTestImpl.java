package com.google.cloud.pso.bq_snapshot_manager.services;

import com.google.cloud.pso.bq_snapshot_manager.entities.JsonMessage;
import com.google.cloud.pso.bq_snapshot_manager.services.pubsub.FailedPubSubMessage;
import com.google.cloud.pso.bq_snapshot_manager.services.pubsub.PubSubPublishResults;
import com.google.cloud.pso.bq_snapshot_manager.services.pubsub.PubSubService;
import com.google.cloud.pso.bq_snapshot_manager.services.pubsub.SuccessPubSubMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PubSubServiceTestImpl implements PubSubService {

    @Override
    public PubSubPublishResults publishTableOperationRequests(String projectId, String topicId, List<JsonMessage> messages) throws IOException, InterruptedException {
        List<SuccessPubSubMessage> successPubSubMessages = new ArrayList<>(messages.size());
        for(JsonMessage msg: messages){
            successPubSubMessages.add(
                    new SuccessPubSubMessage(msg, "message-id")
            );
        }
        return new PubSubPublishResults(
                successPubSubMessages,
                new ArrayList<FailedPubSubMessage>()
        );
    }
}
