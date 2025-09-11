// /js/board-update-category.js
// 초기 hidden.value로 칩을 그려주고, /api/categories로 목록을 불러와 피커를 서버 데이터로 교체
(() => {
  const btn = document.getElementById("btnAddCategory");
  const picker = document.getElementById("categoryPicker");
  const chipList = document.getElementById("chipList");
  const hidden = document.getElementById("categoryHidden");
  if (!btn || !picker || !chipList || !hidden) return;

  // ---------- 공통 유틸 ----------
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

  function drawChipFromHidden() {
    const current = (hidden.value || "").trim();
    chipList.innerHTML = "";
    if (current) {
      chipList.appendChild(createChip(current));
      disableAdd(true);
    } else {
      disableAdd(false);
    }
  }

  function selectCategory(name) {
    if (chipList.children.length > 0) return; // 1개 고정
    chipList.appendChild(createChip(name));
    hidden.value = name; // 서버가 이름 문자열을 받는 시그니처
    picker.hidden = true;
    disableAdd(true);
    // 강조 갱신
    [...picker.querySelectorAll(".cat-option")].forEach((b) =>
        b.classList.toggle("is-selected", b.textContent.trim() === name)
    );
  }

  function clearSelection() {
    chipList.innerHTML = "";
    hidden.value = "";
    disableAdd(false);
    [...picker.querySelectorAll(".cat-option")].forEach((b) =>
        b.classList.remove("is-selected")
    );
  }

  // ---------- Ajax ----------
  async function loadCategories() {
    try {
      const res = await fetch("/api/categories", {
        headers: { Accept: "application/json" },
        credentials: "same-origin", // 세션 쿠키 포함
      });
      if (res.status === 401) {
        location.href = "/login";
        return;
      }
      if (!res.ok) throw new Error("카테고리 조회 실패");
      const items = await res.json(); // [{id, category}]
      renderPicker(items);

      // 현재 선택 강조
      const current = (hidden.value || "").trim();
      if (current) {
        [...picker.querySelectorAll(".cat-option")].forEach((b) =>
            b.classList.toggle("is-selected", b.textContent.trim() === current)
        );
      }
    } catch (e) {
      console.error(e);
      alert("카테고리 목록을 불러오지 못했습니다.");
    }
  }

  async function createCategory(name) {
    try {
      const res = await fetch("/api/categories", {
        method: "POST",
        headers: { "Content-Type": "application/json", Accept: "application/json" },
        credentials: "same-origin",
        body: JSON.stringify({ category: name }),
      });
      if (res.status === 401) {
        location.href = "/login";
        return null;
      }
      if (!res.ok) {
        const msg = await res.text();
        alert(msg || "카테고리를 추가할 수 없습니다.");
        return null;
      }
      return await res.json(); // {id, category}
    } catch (e) {
      console.error(e);
      alert("네트워크 오류로 실패했습니다.");
      return null;
    }
  }

  // ---------- UI 렌더 ----------
  function renderPicker(items) {
    picker.innerHTML = "";

    // 옵션 리스트
    const list = document.createElement("div");
    list.className = "cat-list";
    items.forEach((it) => {
      const b = document.createElement("button");
      b.type = "button";
      b.className = "cat-option";
      b.textContent = it.category;
      b.dataset.category = it.category;
      list.appendChild(b);
    });

    picker.appendChild(list);

  }

  // ---------- 이벤트 ----------
  // 초기 칩(서버 hidden 값) 렌더
  drawChipFromHidden();

  // 토글
  btn.addEventListener("click", () => {
    if (btn.disabled) return;
    picker.hidden = !picker.hidden;
    btn.setAttribute("aria-expanded", (!picker.hidden).toString());
  });

  // 옵션/생성 클릭 위임
  picker.addEventListener("click", async (e) => {
    // 옵션 선택
    if (e.target.classList.contains("cat-option")) {
      const name = e.target.textContent.trim();
      selectCategory(name);
      return;
    }
    // 새 카테고리 생성
    if (e.target.id === "newCatBtn") {
      const input = picker.querySelector("#newCatInput");
      const name = (input?.value || "").trim();
      if (!name) return input?.focus();

      const saved = await createCategory(name);
      if (!saved) return;

      const list = picker.querySelector(".cat-list");
      const b = document.createElement("button");
      b.type = "button";
      b.className = "cat-option";
      b.textContent = saved.category;
      b.dataset.category = saved.category;
      list?.prepend(b);

      selectCategory(saved.category);
      input.value = "";
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

  // ---------- 시작 ----------
  loadCategories();
})();
