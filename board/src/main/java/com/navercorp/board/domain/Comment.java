package com.navercorp.board.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long commentId;

	@Column(nullable = false)
	private String content;

	@CreationTimestamp
	@Column(nullable = false)
	private LocalDateTime createTime;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "groupId")
	private Group group;

	@ManyToOne
	@JoinColumn(name = "userId")
	private User user;

	public Comment(User user, Group group, String content) {
		this.user = user;
		this.group = group;
		this.content = content;
		
		user.getCommentList().add(this);
		group.getCommentList().add(this);
	}
}
