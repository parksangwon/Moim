package com.navercorp.board.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "groups")
@Data
@NoArgsConstructor
public class Group {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long groupId;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "userId")
	private User user;

	@Column(nullable = true)
	private String imgAddr;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String recruitingPolicy;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	@Column(name = "project_start_datetime", nullable = false)
	private LocalDateTime psDate;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	@Column(name = "project_finish_datetime", nullable = false)
	private LocalDateTime pfDate;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	@Column(name = "recruiting_start_datetime", nullable = false)
	private LocalDateTime rsDate;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	@Column(name = "recruiting_finish_datetime", nullable = false)
	private LocalDateTime rfDate;

	@Column(nullable = false)
	private Integer personnel;

	@Column(nullable = false)
	private String category;

	@Column(nullable = false)
	private String address;

	@Column(nullable = false)
	private String addressDo;

	@Column(nullable = false)
	private String simpleInfo;

	@Column(nullable = true)
	private String detailInfo;

	@CreationTimestamp
	@Column(updatable = false, nullable = false)
	private LocalDateTime createTime;

	@UpdateTimestamp
	@Column(nullable = false)
	private LocalDateTime modifyTime;

	@OneToMany(mappedBy = "group", cascade = CascadeType.REMOVE)
	@OrderBy("applyTime asc")
	private List<Apply> applyList = new ArrayList<Apply>();

	@OneToMany(mappedBy = "group", cascade = CascadeType.REMOVE)
	@OrderBy("createTime asc")
	private List<Comment> commentList = new ArrayList<Comment>();

	@OneToMany(mappedBy = "group", cascade = CascadeType.REMOVE)
	private List<Recommend> recommendList = new ArrayList<Recommend>();

	public void setUser(User user) {
		this.user = user;
		
		user.getGroupList().add(this);
	}

	public boolean hasApplyUser(Long userId) {
		for (Apply apply : this.applyList) {
			if (apply.getUser().getUserId().equals(userId)) {
				return true;
			}
		}
		return false;
	}

	public void update(Group group) {
		this.title = group.getTitle();
		this.recruitingPolicy = group.getRecruitingPolicy();
		this.psDate = group.getPsDate();
		this.pfDate = group.getPfDate();
		this.rsDate = group.getRsDate();
		this.rfDate = group.getRfDate();
		this.personnel = group.getPersonnel();
		this.category = group.getCategory();
		this.address = group.getAddress();
		this.simpleInfo = group.getSimpleInfo();
		this.detailInfo = group.getDetailInfo();
	}

	public boolean hasRecommendUser(Long userId) {
		for (Recommend recommend : this.recommendList) {
			if (recommend.getUser().getUserId().equals(userId)) {
				return true;
			}
		}
		return false;
	}

	public void removeApply(Apply storedApply) {
		for (int i = 0; i < this.applyList.size(); i++) {
			Apply apply = this.applyList.get(i);
			if (apply.getApplyId().equals(storedApply.getApplyId())) {
				this.applyList.remove(i);
			}
		}
	}
}
