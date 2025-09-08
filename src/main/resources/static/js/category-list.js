document.addEventListener("DOMContentLoaded", () => {
  const categoryList = document.getElementById("categoryList");
  const newCategoryInput = document.getElementById("newCategoryInput");
  const createCategoryBtn = document.getElementById("createCategoryBtn");
  const deleteModal = document.getElementById("deleteModal");
  const confirmDeleteBtn = document.getElementById("confirmDeleteBtn");

  let deleteTarget = null;

  /* ---------- 생성 ---------- */
  createCategoryBtn.addEventListener("click", () => {
    const name = newCategoryInput.value.trim();
    if (!name) return;
    categoryList.appendChild(makeRow(name, "(0개 게시물)"));
    newCategoryInput.value = "";
  });

  /* ---------- 리스트 내 이벤트 위임 ---------- */
  categoryList.addEventListener("click", (e) => {
    const row = e.target.closest(".catmg-row");
    if (!row) return;

    // 수정 시작
    if (e.target.closest(".js-edit")) {
      startInlineEdit(row);
      return;
    }

    // 삭제 요청
    if (e.target.closest(".js-delete")) {
      deleteTarget = row;
      deleteModal.classList.remove("is-hidden");
      return;
    }

    // 저장
    if (e.target.closest(".js-save")) {
      saveInlineEdit(row);
      return;
    }

    // 취소
    if (e.target.closest(".js-cancel")) {
      cancelInlineEdit(row);
      return;
    }
  });

  /* ---------- 모달 ---------- */
  deleteModal.addEventListener("click", (e) => {
    if (e.target.dataset.close === "true") {
      deleteModal.classList.add("is-hidden");
      deleteTarget = null;
    }
  });

  confirmDeleteBtn.addEventListener("click", () => {
    if (deleteTarget) deleteTarget.remove();
    deleteTarget = null;
    deleteModal.classList.add("is-hidden");
  });

  /* ================= helpers ================= */

  function makeRow(name, countText) {
    const row = document.createElement("div");
    row.className = "catmg-row";
    row.appendChild(makeLeft(name, countText));
    row.appendChild(makeActions(name));
    return row;
  }

  function makeLeft(name, countText) {
    const left = document.createElement("div");
    left.className = "catmg-left";
    left.innerHTML = `
        <span class="catmg-chip">${escapeHtml(name)}</span>
        <span class="catmg-count">${escapeHtml(countText)}</span>
      `;
    return left;
  }

  function makeActions(ariaName) {
    const actions = document.createElement("div");
    actions.className = "catmg-actions";
    actions.innerHTML = `
        <button class="catmg-icon-btn js-edit" aria-label="수정(${escapeAttr(
          ariaName
        )})">
          <i class="fa-solid fa-pen"></i>
        </button>
        <button class="catmg-icon-btn js-delete" aria-label="삭제(${escapeAttr(
          ariaName
        )})">
          <i class="fa-solid fa-trash"></i>
        </button>
      `;
    return actions;
  }

  function startInlineEdit(row) {
    const left = row.querySelector(".catmg-left");
    const chip = left.querySelector(".catmg-chip");
    const count = left.querySelector(".catmg-count");

    const originalName = chip?.textContent?.trim() ?? "";
    const countText = count?.textContent?.trim() ?? "(0개 게시물)";

    // 원본 저장 (취소 복원용)
    row.dataset.originalName = originalName;
    row.dataset.originalCount = countText;

    // 왼쪽 영역만 인라인 편집 폼으로 교체 (액션 영역은 그대로 둠 → 스타일/핸들러 유지)
    left.innerHTML = `
        <div class="catmg-inline">
          <input type="text" class="catmg-input" value="${escapeAttr(
            originalName
          )}" aria-label="카테고리 이름 편집" />
          <button class="catmg-btn catmg-btn--primary js-save">저장</button>
          <button class="catmg-btn catmg-btn--ghost js-cancel">취소</button>
        </div>
      `;

    // 인라인 수정 중에는 액션 아이콘 비활성화
    setRowActionsDisabled(row, true);

    const input = left.querySelector("input");
    input.focus();
    input.setSelectionRange(input.value.length, input.value.length);

    // Enter=저장, Esc=취소
    input.addEventListener(
      "keydown",
      (ev) => {
        if (ev.key === "Enter") {
          ev.preventDefault();
          saveInlineEdit(row);
        } else if (ev.key === "Escape") {
          ev.preventDefault();
          cancelInlineEdit(row);
        }
      },
      { once: true }
    );
  }

  function saveInlineEdit(row) {
    const input = row.querySelector(".catmg-left input");
    const newName =
      (input?.value || "").trim() || row.dataset.originalName || "이름없음";
    const countText = row.dataset.originalCount || "(0개 게시물)";

    // 왼쪽 영역만 다시 표시
    const newLeft = makeLeft(newName, countText);
    row.querySelector(".catmg-left").replaceWith(newLeft);

    // 액션 아이콘 다시 활성화 (스타일은 그대로 유지됨)
    setRowActionsDisabled(row, false);

    // 데이터 정리
    delete row.dataset.originalName;
    delete row.dataset.originalCount;
  }

  function cancelInlineEdit(row) {
    const originalName = row.dataset.originalName || "이름없음";
    const countText = row.dataset.originalCount || "(0개 게시물)";

    // 왼쪽 영역만 복원
    const left = makeLeft(originalName, countText);
    row.querySelector(".catmg-left").replaceWith(left);

    // 아이콘 재활성화
    setRowActionsDisabled(row, false);

    delete row.dataset.originalName;
    delete row.dataset.originalCount;
  }

  function setRowActionsDisabled(row, disabled) {
    const btns = row.querySelectorAll(".catmg-icon-btn");
    btns.forEach((btn) => {
      btn.toggleAttribute("disabled", disabled);
      btn.setAttribute("aria-disabled", String(disabled));
      btn.tabIndex = disabled ? -1 : 0;
      btn.classList.toggle("is-disabled", disabled);
      // pointer-events로 클릭 차단 보강 (브라우저별)
      if (disabled) {
        btn.style.pointerEvents = "none";
      } else {
        btn.style.pointerEvents = "";
      }
    });
  }

  function escapeHtml(str) {
    return String(str)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#39;");
  }
  function escapeAttr(str) {
    return escapeHtml(str).replace(/`/g, "&#96;");
  }
});
