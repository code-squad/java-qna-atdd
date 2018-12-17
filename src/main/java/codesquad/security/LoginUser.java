package codesquad.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
    @Retention
        자바 컴파일러가 어노테이션을 다루는 방법, 어노테이션의 생명주기를 결정
            - RetentionPolicy.SOURCE : 컴파일 전까지만 유효. (컴파일 이후에는 사라짐)
            - RetentionPolicy.CLASS : 컴파일러가 클래스를 참조할 때까지 유효.
            - RetentionPolicy.RUNTIME : 컴파일 이후에도 JVM에 의해 계속 참조가 가능. (리플렉션 사용)
    @Target
        어노테이션이 적용할 위치를 결정
            - ElementType.PACKAGE : 패키지 선언
            - ElementType.TYPE : 타입 선언
            - ElementType.ANNOTATION_TYPE : 어노테이션 타입 선언
            - ElementType.CONSTRUCTOR : 생성자 선언
            - ElementType.FIELD : 멤버 변수 선언
            - ElementType.LOCAL_VARIABLE : 지역 변수 선언
            - ElementType.METHOD : 메서드 선언
            - ElementType.PARAMETER : 전달인자 선언
            - ElementType.TYPE_PARAMETER : 전달인자 타입 선언
            - ElementType.TYPE_USE : 타입 선언
*/

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginUser {
    boolean required() default true;
}
