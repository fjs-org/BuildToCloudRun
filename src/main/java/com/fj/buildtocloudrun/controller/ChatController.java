package com.fj.buildtocloudrun.controller;

import com.fj.buildtocloudrun.dtos.ChatPost;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@RestController
@Tag(name = "Chat Management", description = "Endpoints for sending and receiving chat messages via Long Polling")
public class ChatController {
    private static final Log LOGGER = LogFactory.getLog(ChatController.class);

    private final Queue<DeferredResult<List<ChatPost>>> waitingRequests = new ConcurrentLinkedQueue<>();

    // TODO: To be held in some storage (Firestore, Cache etc. ore Redis-Stream)
    private final List<ChatPost> chatMainHolder = new ArrayList<>();

    @Operation(
            summary = "Poll for new messages",
            description = "This is a Long Polling endpoint. The connection stays open until a new message is posted or it times out after 30 seconds."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully received a new message"),
            @ApiResponse(responseCode = "503", description = "Request timed out (no message sent during the interval)")
    })
    @GetMapping("/chatpost")
    @SecurityRequirement(name = "Authorization")
    public DeferredResult<List<ChatPost>> pollForMessages() {
        LOGGER.info("Start polling for new chatposts");
        DeferredResult<List<ChatPost>> deferredResult = new DeferredResult<>(10000L);

        deferredResult.onTimeout(() -> {
            LOGGER.info("Get chatpost is timing out");
            waitingRequests.remove(deferredResult);
        });
        deferredResult.onCompletion(() -> waitingRequests.remove(deferredResult));

        waitingRequests.add(deferredResult);
        return deferredResult;
    }

    @Operation(
            summary = "Post a new message",
            description = "Creates a new chat post and immediately broadcasts it to all clients currently waiting on the poll endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message posted and broadcasted successfully")
    })
    @PostMapping("/chatpost")
    @SecurityRequirement(name = "Authorization")
    public ChatPost createPost(@RequestBody ChatPost post, Principal principal) {
        LOGGER.info("Start creating new chatpost");

        ChatPost mappedChatPost = new ChatPost(UUID.randomUUID(), principal.getName(), LocalDateTime.now(), post.message());
        chatMainHolder.add(mappedChatPost);

        while (!waitingRequests.isEmpty()) {
            LOGGER.info("Iterating through waiting Requests");
            DeferredResult<List<ChatPost>> result = waitingRequests.poll();
            if (result != null) {
                result.setResult(chatMainHolder);
            }
        }
        return mappedChatPost;
    }
}