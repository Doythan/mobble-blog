// /js/board-save-category.js (Ajax 버전)
(() => {
  const btn = document.getElementById("btnAddCategory");
  const picker = document.getElementById("categoryPicker");
  const chipList = document.getElementById("chipList");
  const hidden = document.getElementById("categoryHidden");
  if (!btn || !picker || !chipList || !hidden) return;

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

  function selectCategory(name) {
    if (chipList.children.length > 0) return; // 1개 고정
    chipList.appendChild(createChip(name));
    hidden.value = name; // 서버가 이름 문자열을 받는 시그니처임
    picker.hidden = true;
    disableAdd(true);
    // 강조 토글
    [...picker.querySelectorAll(".cat-option")].forEach((b) =>
        b.classList.toggle("is-selected", b.textContent.trim() === name)
    );
  }

  function clearSelection() {
    chipList.innerHTML = "";
    hidden.value = "";
    disableAdd(false);
    [...picker.querySelectorAll(".cat-option")].forEach((b) => b.classList.remove("is-selected"));
  }

  // 토글 (비활성화면 열리지 않음)
  btn.addEventListener("click", () => {
    if (btn.disabled) return;
    picker.hidden = !picker.hidden;
    btn.setAttribute("aria-expanded", (!picker.hidden).toString());
  });

  // 옵션/생성 클릭 위임
  picker.addEventListener("click", (e) => {
    if (e.target.classList.contains("cat-option")) {
      const name = e.target.textContent.trim();
      selectCategory(name);
    }
  });

  // 칩 X → 해제
  chipList.addEventListener("click", (e) => {
    if (!e.target.classList.contains("chip-x")) return;
    e.target.closest(".chip")?.remove();
    clearSelection();
  });

  // 외부 클릭 → 피커 닫기
  document.addEventListener("click", (e) => {
    if (!picker.hidden && !picker.contains(e.target) && e.target !== btn && !btn.contains(e.target)) {
      picker.hidden = true;
      btn.setAttribute("aria-expanded", "false");
    }
  });

  // Ajax: 내 카테고리 로드
  async function loadCategories() {
    try {
      const res = await fetch("/api/categories", {
        headers: { Accept: "application/json" },
        credentials: "same-origin", // 세션 쿠키 포함 (동일 도메인)
      });
      if (res.status === 401) {
        // 로그인 필요
        location.href = "/login";
        return;
      }
      if (!res.ok) throw new Error("카테고리 조회 실패");
      const items = await res.json(); // [{id, category}]
      renderPicker(items);
    } catch (e) {
      console.error(e);
      alert("카테고리 목록을 불러오지 못했습니다.");
    }
  }

  function renderPicker(items) {
    // 더미 버튼 제거 후 서버 데이터로 채움
    picker.innerHTML = "";
    const listWrap = document.createElement("div");
    listWrap.className = "cat-list";

    items.forEach((it) => {
      const b = document.createElement("button");
      b.type = "button";
      b.className = "cat-option";
      b.textContent = it.category;
      b.dataset.category = it.category;
      listWrap.appendChild(b);
    });
    picker.appendChild(listWrap);
  }

  disableAdd(false);
  loadCategories();
})();
