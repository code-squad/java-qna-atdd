INSERT INTO user (id, user_id, password, name, email) values (1, 'ksm0814', 'k5696', 'link', 'kksm0814@naver.com');
INSERT INTO user (id, user_id, password, name, email) values (2, 'sanjigi', 'test', '산지기', 'sanjigi@slipp.net');

INSERT INTO question (id, writer_id, title, contents, create_date, deleted) VALUES (1, 1, 'ksm0814 1번 사용자의 질문', '1번 사용자의 내용이에용.', CURRENT_TIMESTAMP(), false);
INSERT INTO question (id, writer_id, title, contents, create_date, deleted) VALUES (2, 1, 'ksm0814 1번 사용자의 질문2', '내용 두번째입니다.', CURRENT_TIMESTAMP(), false);

INSERT INTO answer (writer_id, contents, create_date, question_id, deleted) VALUES (1, 'http://underscorejs.org/docs/underscore.html Underscore.js 강추합니다! 쓸일도 많고, 코드도 길지 않고, 자바스크립트의 언어나 기본 API를 보완하는 기능들이라 자바스크립트 이해에 도움이 됩니다. 무엇보다 라이브러리 자체가 아주 유용합니다.', CURRENT_TIMESTAMP(), 1, false);
INSERT INTO answer (writer_id, contents, create_date, question_id, deleted) VALUES (2, '언더스코어 강력 추천드려요. 다만 최신 버전을 공부하는 것보다는 0.10.0 버전부터 보는게 더 좋더군요. 코드의 변천사도 알 수 있고, 최적화되지 않은 코드들이 기능은 그대로 두고 최적화되어 가는 걸 보면 재미가 있습니다 :)', CURRENT_TIMESTAMP(), 1, false);
