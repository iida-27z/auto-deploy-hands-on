package com.example.demo.domain.birthday;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Birthdayドメインモデルの計算ロジックを確認するテストです。 */
class BirthdayTests {

  /** 指定日基準で年齢を計算できることを確認します。 */
  @Test
  @DisplayName("指定した基準日で年齢を計算できる")
  void calculateAgeReturnsExpectedValue() {
    // arrange
    Birthday birthday = new Birthday(1L, "田中 花子", LocalDate.of(1995, 1, 12), null);

    // act
    int age = birthday.calculateAge(LocalDate.of(2026, 11, 24));

    // assert
    assertThat(age).isEqualTo(31);
  }

  /** 次の誕生日までの日数を計算できることを確認します。 */
  @Test
  @DisplayName("次の誕生日までの日数を計算できる")
  void calculateDaysUntilBirthdayReturnsExpectedValue() {
    // arrange
    Birthday birthday = new Birthday(1L, "田中 花子", LocalDate.of(1995, 1, 12), null);

    // act
    long days = birthday.calculateDaysUntilBirthday(LocalDate.of(2026, 11, 24));

    // assert
    assertThat(days).isEqualTo(49);
  }

  /** 当日は誕生日まで0日になることを確認します。 */
  @Test
  @DisplayName("誕生日当日は誕生日まで0日になる")
  void calculateDaysUntilBirthdayReturnsZeroOnBirthday() {
    // arrange
    Birthday birthday = new Birthday(1L, "田中 花子", LocalDate.of(1995, 1, 12), null);

    // act
    long days = birthday.calculateDaysUntilBirthday(LocalDate.of(2026, 1, 12));

    // assert
    assertThat(days).isZero();
  }
}
