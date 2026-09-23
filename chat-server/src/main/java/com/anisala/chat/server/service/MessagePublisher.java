
package com.anisala.chat.server.service;

import com.anisala.chat.server.model.Message;

public interface MessagePublisher {
    boolean publish(Message message);
}