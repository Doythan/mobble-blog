(() => {
  const form = document.getElementById('writeForm');
  if (!form) return; // 이 페이지가 아니면 조용히 종료

  form.addEventListener('submit', (e) => {
    // 필요한 경우, 기본 유효성 체크
    const title = form.querySelector('input[name="title"]')?.value?.trim();
    const category = document.getElementById('categoryHidden')?.value?.trim();

    if (!title) {
      e.preventDefault();
      alert('제목을 입력해 주세요.');
      form.querySelector('input[name="title"]')?.focus();
      return;
    }
    if (!category) {
      e.preventDefault();
      alert('카테고리를 선택해 주세요.');
      return;
    }
  });

  // 버튼이 있으면 지원 (선택적)
  const btn = document.getElementById('btnPublish');
  if (btn) btn.addEventListener('click', () => form.submit());
})();

