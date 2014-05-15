package com.amlcurran.messages.loaders;

public interface MessagesLoader {
    void loadConversationList(CursorLoadListener listener);

    void loadThread(String threadId, CursorLoadListener loadListener);
}
