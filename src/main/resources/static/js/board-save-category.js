// 카테고리: 1개만 선택 / 선택 후 버튼 비활성화 / 칩의 X로 제거 시 다시 활성화
(function () {
  const btn = document.getElementById("btnAddCategory");
  const picker = document.getElementById("categoryPicker");
  const list = document.getElementById("chipList");
  const hidden = document.getElementById("categoryHidden");

  function disableAdd(disabled) {
    btn.disabled = disabled;
    btn.classList.toggle("disabled", disabled);
    btn.setAttribute("aria-expanded", (!disabled && !picker.hidden).toString());
  }

  // 버튼 토글(비활성화 상태에서는 열리지 않음)
  btn.addEventListener("click", () => {
    if (btn.disabled) return;
    picker.hidden = !picker.hidden;
    btn.setAttribute("aria-expanded", (!picker.hidden).toString());
  });

  // 옵션 클릭 → 칩 생성 + 버튼 비활성화 + 피커 닫기
  picker.addEventListener("click", (e) => {
    if (!e.target.classList.contains("cat-option")) return;
    const name = e.target.textContent.trim();

    // 이미 선택돼 있으면 무시(1개 고정)
    if (list.children.length > 0) return;

    // 칩 생성 (X 포함)
    const chip = document.createElement("span");
    chip.className = "chip gray";
    chip.innerHTML = `
      <span class="chip-label">${name}</span>
      <button type="button" class="chip-x" aria-label="카테고리 제거">×</button>
    `;
    list.appendChild(chip);

    hidden.value = name;
    picker.hidden = true;
    disableAdd(true);
  });

  // 칩의 X로 제거 → 버튼 재활성화
  list.addEventListener("click", (e) => {
    if (!e.target.classList.contains("chip-x")) return;
    const chip = e.target.closest(".chip");
    if (chip) chip.remove();
    hidden.value = "";
    disableAdd(false);
  });
})();
