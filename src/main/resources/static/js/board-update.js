(() => {
  const form = document.getElementById('updateForm');
  if (!form) return; // 이 페이지가 아니면 조용히 종료

  // 선택 버튼이 있을 때만 사용
  const btn = document.getElementById('btnUpdate');
  if (btn) btn.addEventListener('click', () => form.submit());

  // 최소 검증(제목/카테고리)
  form.addEventListener('submit', (e) => {
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
})();
