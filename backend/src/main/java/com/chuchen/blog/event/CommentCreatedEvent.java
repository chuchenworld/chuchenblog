package com.chuchen.blog.event;

import com.chuchen.blog.entity.Comment;
import com.chuchen.blog.entity.Photo;
import com.chuchen.blog.entity.Post;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CommentCreatedEvent extends ApplicationEvent {

    private final Comment comment;
    private final Post post;
    private final Photo photo;
    private final String recipientEmail;

    public CommentCreatedEvent(Object source, Comment comment, Post post, String recipientEmail) {
        super(source);
        this.comment = comment;
        this.post = post;
        this.photo = null;
        this.recipientEmail = recipientEmail;
    }

    public CommentCreatedEvent(Object source, Comment comment, Photo photo, String recipientEmail, boolean isPhoto) {
        super(source);
        this.comment = comment;
        this.post = null;
        this.photo = photo;
        this.recipientEmail = recipientEmail;
    }
}
