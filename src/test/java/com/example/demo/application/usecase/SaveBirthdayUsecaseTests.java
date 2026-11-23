package com.example.demo.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.application.dto.SaveCommand;
import com.example.demo.domain.birthday.Birthday;
import com.example.demo.domain.birthday.BirthdayRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** SaveBirthdayUsecaseの保存・検証ロジックを確認するテストです。 */
@ExtendWith(MockitoExtension.class)
class SaveBirthdayUsecaseTests {

  @Mock private BirthdayRepository birthdayRepository;

  private SaveBirthdayUsecase saveBirthdayUsecase;

  /** 各テストの初期状態を構築します。 */
  @BeforeEach
  void setUp() {
    // arrange
    Clock fixedClock = Clock.fixed(Instant.parse("2026-11-24T00:00:00Z"), ZoneId.of("Asia/Tokyo"));
    saveBirthdayUsecase = new SaveBirthdayUsecase(birthdayRepository, fixedClock);
  }

  /** 未来日の生年月日は保存できないことを確認します。 */
  @Test
  @DisplayName("未来日の生年月日は保存できない")
  void executeRejectsFutureBirthDate() {
    // arrange
    SaveCommand command = new SaveCommand(null, "未来 太郎", LocalDate.of(2099, 1, 1), null);

    // act & assert
    assertThatThrownBy(() -> saveBirthdayUsecase.execute(command))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("未来日");

    // assert
    verify(birthdayRepository, never()).save(any(Birthday.class));
  }

  /** 更新対象が存在しない場合に保存できないことを確認します。 */
  @Test
  @DisplayName("更新対象が存在しない場合は保存できない")
  void executeRejectsUpdateWhenTargetDoesNotExist() {
    // arrange
    when(birthdayRepository.findById(99L)).thenReturn(Optional.empty());
    SaveCommand command = new SaveCommand(99L, "更新 太郎", LocalDate.of(2001, 1, 1), null);

    // act & assert
    assertThatThrownBy(() -> saveBirthdayUsecase.execute(command))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("存在しません");

    // assert
    verify(birthdayRepository, never()).save(any(Birthday.class));
  }

  /** 正常入力で保存処理が呼び出されることを確認します。 */
  @Test
  @DisplayName("正常な入力で誕生日を保存できる")
  void executeSavesBirthday() {
    // arrange
    SaveCommand command = new SaveCommand(null, "保存 太郎", LocalDate.of(2001, 1, 1), "メモ");
    when(birthdayRepository.save(any(Birthday.class))).thenReturn(1);

    // act
    saveBirthdayUsecase.execute(command);

    // assert
    verify(birthdayRepository).save(any(Birthday.class));
  }

  /** 更新時に対象が競合削除されて0件更新になった場合に保存できないことを確認します。 */
  @Test
  @DisplayName("更新対象が競合削除されて0件更新になった場合は保存できない")
  void executeRejectsUpdateWhenTargetIsDeletedAfterLookup() {
    // arrange
    when(birthdayRepository.findById(99L))
        .thenReturn(Optional.of(new Birthday(99L, "更新前 太郎", LocalDate.of(2000, 1, 1), null)));
    when(birthdayRepository.save(any(Birthday.class))).thenReturn(0);
    SaveCommand command = new SaveCommand(99L, "更新 太郎", LocalDate.of(2001, 1, 1), null);

    // act & assert
    assertThatThrownBy(() -> saveBirthdayUsecase.execute(command))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("存在しません");
  }
}
