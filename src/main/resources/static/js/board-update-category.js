(function () {
  const btn = document.getElementById("btnAddCategory");
  const picker = document.getElementById("categoryPicker");
  const list = document.getElementById("chipList");
  const hidden = document.getElementById("categoryHidden");

  const ALLOWED = ["java", "springboot", "docker"]; // 더미 목록

  function disableAdd(disabled) {
    btn.disabled = disabled;
    btn.classList.toggle("disabled", disabled);
    btn.setAttribute("aria-expanded", (!disabled && !picker.hidden).toString());
  }

  function createChip(name) {
    const chip = document.createElement("span");
    chip.className = "chip gray";
    chip.innerHTML = `
        <span class="chip-label">${name}</span>
        <button type="button" class="chip-x" aria-label="카테고리 제거">×</button>
      `;
    return chip;
  }

  // 🔹 초기 렌더: 서버에서 받은 카테고리명으로 칩 미리 만들기
  (function initialRender() {
    const current = (hidden.value || "").trim();
    if (!current) {
      disableAdd(false);
      return;
    }

    // 더미 옵션에 있든 없든, 현재 값이 있으면 칩 생성
    list.innerHTML = "";
    list.appendChild(createChip(current));
    disableAdd(true);

    // 피커 안의 일치 옵션을 강조하고 싶다면:
    [...picker.querySelectorAll(".cat-option")].forEach((btn) => {
      btn.classList.toggle("is-selected", btn.textContent.trim() === current);
    });
  })();

  // 버튼 토글
  btn.addEventListener("click", () => {
    if (btn.disabled) return;
    picker.hidden = !picker.hidden;
    btn.setAttribute("aria-expanded", (!picker.hidden).toString());
  });

  // 옵션 선택 → 한 개만 허용
  picker.addEventListener("click", (e) => {
    if (!e.target.classList.contains("cat-option")) return;
    const name = e.target.textContent.trim();
    if (list.children.length > 0) return;

    list.appendChild(createChip(name));
    hidden.value = name;
    picker.hidden = true;
    disableAdd(true);
  });

  // 칩의 X로 제거 → 버튼 재활성화
  list.addEventListener("click", (e) => {
    if (!e.target.classList.contains("chip-x")) return;
    e.target.closest(".chip")?.remove();
    hidden.value = "";
    disableAdd(false);

    // 피커 강조 리셋
    [...picker.querySelectorAll(".cat-option")].forEach((btn) =>
      btn.classList.remove("is-selected")
    );
  });
})();
