## 유저 API

**POST** /api/login\
요청
```
{
	"email": "<user_email>"
	"password": "<user_password>"
}
```
응답(200) - 로그인 성공
```
"no content"
```
응답(400) - 로그인 실패
```
"이메일이나 비밀번호를 다시 확인해주세요."
```
\
**POST** /api/signUp\
요청
```
{
	"email": "<user_email>"
	"password": "<user_password>"
	"passwordConfirm": "<user_password_confirm>"
	"name": "<user_name>"
	"phone": "<user_phone>"
}
```
응답(200)
```
"회원가입이 완료되었습니다. 로그인 하세요."
```
응답(400) - 이메일이 중복된 경우
```
"사용할 수 없는 이메일 입니다."
```
응답(400) - validation 통과 못한 경우
```
"데이터를 모두 입력하거나 비밀번호를 확인해주세요."
```
\
**PUT** /api/users\
요청 - Id(email)은 Session에서 추출한다.
```
{
	"password" : "<user_password>",
	"name" : "<user_name>",
	"phone" : "<user_password>",
	"company" : "<user_company>",
	"introduction" : "<user_introduction>"
}
```
응답(200)
```
"회원 정보 업데이트 성공"
```
응답(400) - 암호 불일치
```
"비밀번호를 다시 확인해주세요."
```
응답(400) - validation 통과 못한 경우
```
"입력을 다시 확인해주세요."
```
\
**DELETE** /api/users\
요청 - Id(email)은 Session에서 추출한다.
```
{
	"password": "<user_password>"
}
```
응답(200)
```
"이용해 주셔서 감사합니다."
```
응답(400) - 암호 불일치
```
"비밀번호를 다시 확인해주세요."
```
응답(400) - validation 통과 못한 경우
```
"입력을 다시 확인해주세요."
```

## 모임 API

**POST** /api/groups/filtering\
요청
```
{
	"keyword":"<search_keyword>",
	"category":"<category_option>",
	"address":"<area_option>",
	"weekdate":"<weekdate_option>",
	"time":"<time_option>"
}
```
응답(200)
```
[
	{
		"groupId": "<group_id>",
		"title": "<group_title>",
		...
		...
		"applyList": [
			     	      {
			     	               "applyId": "<apply_id>"
			     	               ...
				      }
			     ]
		...
		...
	}
]
```
응답(400) - validation 통과 못한 경우
```
"잘못된 요청입니다."
```
\
**POST** /api/groups\
요청 - Multipart, Id(email)은 Session에서 추출한다.
```
{
	"title": "<group_title>"
	"recruitingPolicy": "<group_recruiting_policy>",
	"psDate": "<group_project_start_date>",
	"pfDate": "<group_project_finish_date>",
	"rsDate": "<group_recruiting_start_date>",
	"rfDate": "<group_recruiting_finish_date>",
	"personnel": "<group_personnel>",
	"category": "<group_category>",
	"address": "<group_address>",
	"simpleInfo": "<group_simple_information>",
	"detailInfo": "<group_detail_information>"
}
```
응답(200)
```
"<group_id>"
```
응답(400) - validation 통과 못한 경우
```
"입력을 다시 확인해주세요."
```
응답(400) - 존재하지 않는 모임이나 유저일 경우
```
"잘못된 요청입니다."
```
\
**DELETE** /api/groups\
요청 - Id(email)은 Session에서 추출한다.
```
{
	"groupId": "<group_id>"
}
```
응답(200)
```
"모임이 삭제되었습니다."
```
응답(400) - 개설자가 아닌 유저가 삭제 요청을 했을 경우
```
"개설자만 모임을 삭제할 수 있습니다."
```
응답(400) - 모임이 존재하지 않는 경우
```
"존재하지 않는 모임이거나 권한이 없습니다."
```

## 모임 신청 API
**POST** /api/applys\
요청 - Id(email)은 Session에서 추출한다.
```
{
	"groupId": "<group_id>"
}
```
응답(200)
```
{
	"condition": "<group_condition>",
	"applyAmount": "<group_applier_amount>"
}
```
응답(400) - 모임이나 유저가 존재하지 않을 경우
```
"유저나 모임이 존재하지 않습니다."
```
응답(400) - 신청 가능한 모임이 아닌 경우
```
"신청할 수 없는 모임입니다."
```
응답(400) - 유효하지 않은 모임이거나 유효하지 않은 유저가 요청한 경우
```
유효하지 않은 모임이나 유저입니다.
```

## 댓글 API
**POST** /api/comment\
요청 - Id(email)은 Session에서 추출한다.
```
{
	"groupId": "<group_id>",
	"content": "<comment_content>"
}
```
응답(200)
```
[
	{
		"commentId": "<comment_id>",
		"content": "<comment_content",
		"createTime": "<comment_createTime>"
		"user": {
			"userId": "<user_id>",
			"email": "<user_email>",
			...
		}
	},
	{
		"commentId": "<comment_id>",
		"content": "<comment_content",
		"createTime": "<comment_createTime>"
		"user": {
			"userId": "<user_id>",
			"email": "<user_email>",
			...
		}
	},
	...
	...
]
```
응답(400) - 모임이나 유저가 존재하지 않는 경우
```
"존재하지 않는 모임이나 사용자 입니다.
```
\
**DELETE** /api/comment\
요청 - Id(email)은 Session에서 추출한다.
```
{
	"groupId": "<group_id>",
	"commentId": "<comment_id>"
}
```
응답(200)
```
no content
```
응답(400) - 모임이나 유저가 존재하지 않는 경우
```
"존재하지 않는 모임이나 사용자입니다."
```

## 추천 API
**POST** /api/recommend\
요청 - Id(email)은 Session에서 추출한다.
```
{
	"groupId": "<group_id>"
}
```
응답(200)
```
{
	"condition": "<group_recommend_condition>",
	"recommendAmount": "<recommend_amount>"
}
```
응답(400) - 모임이나 유저가 존재하지 않는 경우
```
"유저나 모임이 존재하지 않습니다."
```