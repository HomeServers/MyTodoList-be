# GitHub Actions 상태 배지

프로젝트 README에 다음 배지를 추가하여 CI/CD 상태를 표시할 수 있습니다.

## 배지 코드

```markdown
![CI](https://github.com/YOUR_USERNAME/YOUR_REPO/actions/workflows/ci.yml/badge.svg)
![CD](https://github.com/YOUR_USERNAME/YOUR_REPO/actions/workflows/cd.yml/badge.svg)
![Security](https://github.com/YOUR_USERNAME/YOUR_REPO/actions/workflows/security.yml/badge.svg)
```

## 사용 예시

`YOUR_USERNAME`과 `YOUR_REPO`를 실제 GitHub 사용자명과 저장소명으로 변경하세요.

예를 들어, GitHub 사용자명이 `johndoe`이고 저장소명이 `MyTodoList-be`인 경우:

```markdown
![CI](https://github.com/johndoe/MyTodoList-be/actions/workflows/ci.yml/badge.svg)
![CD](https://github.com/johndoe/MyTodoList-be/actions/workflows/cd.yml/badge.svg)
![Security](https://github.com/johndoe/MyTodoList-be/actions/workflows/security.yml/badge.svg)
```

## README 예시

```markdown
# TodoList API

![CI](https://github.com/YOUR_USERNAME/YOUR_REPO/actions/workflows/ci.yml/badge.svg)
![CD](https://github.com/YOUR_USERNAME/YOUR_REPO/actions/workflows/cd.yml/badge.svg)

Spring Boot 기반 TodoList REST API

## 기술 스택
- Spring Boot 3.4.2
- Kotlin 1.9.25
- JDK 17
- MySQL
- Docker

## 빌드 및 실행
...
```

## Docker Hub 배지 (선택사항)

Docker Hub 이미지 정보도 표시할 수 있습니다:

```markdown
![Docker Image Version](https://img.shields.io/docker/v/YOUR_USERNAME/todolist-api?sort=semver)
![Docker Image Size](https://img.shields.io/docker/image-size/YOUR_USERNAME/todolist-api)
![Docker Pulls](https://img.shields.io/docker/pulls/YOUR_USERNAME/todolist-api)
```
