### File 테스트 방법

통합 테스트를 진행한다면 Service 단의 메서드들을 호출하여 진행  
Service 단의 매개변수에 `MultipartFile`이 전달된 후, 서비스 단에서 이를 활용하기 때문에  
MultipartFile만 잘 생성하면 됨.

#### 1. FileTestUtil을 통해 MultipartFile 받아오기

`FileTestUtil.getMultipartFile("파일 이름")`을 통해 `MultiPart` 객체를 받을 수 있다.   
매개 변수로는 `filename`을 전달받는데, "sky", "aws_image"와 같은 값을 전달하면,
내부적으로 "sky.png", "aws_image.png" 로 저장된다.

`InstanceService`에 파일을 전달하여 인스턴스 생성 시의 코드

```java
Long savedInstanceId = instanceService.createInstance(instanceCreateRequest,
        FileTestUtil.getMultipartFile("name"), fileType);
```

<br>

#### 2. Files 객체를 단독으로 생성하고 싶을 때 - FilesService 이용

`MultipartFile`을 통해 토픽/인스턴스의 생성/수정 하는 방법이 아니라, `Files` 엔티티를 만들고 싶을 때에는
`FilesService`의 코드를 사용해야 한다.

1. `FileTestUtil.getMultipartFile("파일이름")`을 통해 MultipartFile을 반환받는다.
2. `public Files uploadFile(MultipartFile receivedFile, String typeStr)`의 매개변수로 전달하면,
   FilesRepository를 통해 저장한 Files 엔티티를 반환받을 수 있다.
3. 이후 Topic, Instance, User의 `setFiles`를 통해 연관관계를 설정하면 된다.

`FileUtilTest`에서 작성한 테스트 코드의 예시이다.
저장 이후 다시 encoding 해도 에러가 발생하지 않는다.

```java

@Test
@DisplayName("FileTestUtil을 통해 받은 MultipartFile을 통해 인코딩 파일을 받을 수 있다")
public void should_getEncodedFiles() {
    //given
    MultipartFile multipartFile = FileTestUtil.getMultipartFile("filename");
    Files files = filesService.uploadFile(multipartFile, "topic");

    //when
    String encoded = FileUtil.encodedImage(files);

    //then
    log.info(encoded);
}

```