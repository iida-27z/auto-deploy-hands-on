package com.example.demo.presentation.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.demo.application.usecase.GetBirthdayUsecase;
import com.example.demo.application.usecase.ListBirthdayUsecase;
import com.example.demo.application.usecase.SaveBirthdayUsecase;
import com.example.demo.domain.birthday.Birthday;
import com.example.demo.presentation.advice.GlobalExceptionHandler;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/** BirthdayControllerの画面遷移を確認するテストです。 */
class BirthdayControllerTests {

  private MockMvc mockMvc;
  private ListBirthdayUsecase listBirthdayUsecase;
  private GetBirthdayUsecase getBirthdayUsecase;
  private SaveBirthdayUsecase saveBirthdayUsecase;

  /** MockMvcとモック依存を初期化します。 */
  @BeforeEach
  void setUp() {
    // arrange
    listBirthdayUsecase = Mockito.mock(ListBirthdayUsecase.class);
    getBirthdayUsecase = Mockito.mock(GetBirthdayUsecase.class);
    saveBirthdayUsecase = Mockito.mock(SaveBirthdayUsecase.class);
    BirthdayController birthdayController =
        new BirthdayController(listBirthdayUsecase, getBirthdayUsecase, saveBirthdayUsecase);
    mockMvc =
        MockMvcBuilders.standaloneSetup(birthdayController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  /** 一覧画面が表示されることを確認します。 */
  @Test
  @DisplayName("一覧画面を表示できる")
  void showListReturnsBirthdayListTemplate() throws Exception {
    // arrange
    when(listBirthdayUsecase.execute()).thenReturn(List.of());

    // act & assert
    mockMvc
        .perform(get("/birthdays"))
        .andExpect(status().isOk())
        .andExpect(view().name("birthday-list"))
        .andExpect(model().attributeExists("birthdays"));
  }

  /** 編集画面で既存データが表示されることを確認します。 */
  @Test
  @DisplayName("編集画面で既存の誕生日を表示できる")
  void showDetailReturnsEditTemplate() throws Exception {
    // arrange
    when(getBirthdayUsecase.findById(1L))
        .thenReturn(
            java.util.Optional.of(new Birthday(1L, "田中 花子", LocalDate.of(1995, 1, 12), "メモ")));

    // act & assert
    mockMvc
        .perform(get("/birthdays/detail").param("id", "1"))
        .andExpect(status().isOk())
        .andExpect(view().name("birthday-detail"))
        .andExpect(model().attributeExists("form"));
  }

  /** 保存時に入力不備がある場合は詳細画面に戻ることを確認します。 */
  @Test
  @DisplayName("保存時の入力不備がある場合は詳細画面に戻る")
  void saveReturnsDetailTemplateOnValidationError() throws Exception {
    // arrange

    // act & assert
    mockMvc
        .perform(
            post("/birthdays/save").param("mode", "add").param("name", "").param("birthDate", ""))
        .andExpect(status().isOk())
        .andExpect(view().name("birthday-detail"))
        .andExpect(model().attributeExists("errors"));
  }

  @Test
  @DisplayName("更新モードでIDがない場合は保存できない")
  void saveRejectsEditModeWithoutId() throws Exception {
    // arrange

    // act & assert
    mockMvc
        .perform(
            post("/birthdays/save")
                .param("mode", "edit")
                .param("name", "田中 花子")
                .param("birthDate", "1995-01-12"))
        .andExpect(status().isOk())
        .andExpect(view().name("birthday-detail"))
        .andExpect(
            model().attribute("errors", org.hamcrest.Matchers.hasItem("更新対象のIDが指定されていません。")));
  }

  @Test
  @DisplayName("新規登録モードでIDがある場合は保存できない")
  void saveRejectsAddModeWithId() throws Exception {
    // arrange

    // act & assert
    mockMvc
        .perform(
            post("/birthdays/save")
                .param("mode", "add")
                .param("id", "1")
                .param("name", "田中 花子")
                .param("birthDate", "1995-01-12"))
        .andExpect(status().isOk())
        .andExpect(view().name("birthday-detail"))
        .andExpect(model().attribute("errors", org.hamcrest.Matchers.hasItem("新規登録ではIDを指定できません。")));
  }

  @Test
  @DisplayName("詳細画面でIDの型が不正な場合はエラー画面を表示する")
  void showDetailRejectsInvalidIdType() throws Exception {
    // arrange

    // act & assert
    mockMvc
        .perform(get("/birthdays/detail").param("id", "abc"))
        .andExpect(status().isBadRequest())
        .andExpect(view().name("error"));
  }
}
