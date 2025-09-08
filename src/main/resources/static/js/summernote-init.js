// Summernote 초기화
$(function () {
  $("#editor").summernote({
    placeholder: "여기에 내용을 입력하세요…",
    height: 520,
    disableResizeEditor: true,
    toolbar: [
      ["font", ["bold", "italic", "underline", "strikethrough"]],
      ["para", ["ul", "ol"]],
      ["insert", ["link", "table"]], // 이미지/비디오는 제외
      // view 그룹 전부 제외 (codeview, fullscreen, help)
    ],
  });
});
