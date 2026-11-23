package com.example.demo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/** アプリケーション起動確認のテストです。 */
@SpringBootTest
class DemoApplicationTests {

  /** Springコンテキストが正常に起動できることを確認します。 */
  @Test
  @DisplayName("Springコンテキストが正常に起動する")
  void contextLoads() {
    // act & assert
  }
}
