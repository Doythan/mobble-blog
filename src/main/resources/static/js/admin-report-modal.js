/* =========================================================
   Report Manage Page JS (rmg-)
   - 행 클릭 → 관리 모달
   - 신고 상태만 변경 가능
   - 나머지는 모두 "읽기전용 스타일"(disabled 사용 안 함)
   - '기타 사유'는 항상 readOnly, 사유가 '기타'가 아니면 더 어둡게 표시
   - 적용 버튼은 '상태'가 변경될 때만 활성화
   ========================================================= */

document.addEventListener("DOMContentLoaded", () => {
  const tbody = document.getElementById("rmgTbody");
  const modal = document.getElementById("rmgModal");
  const form = document.getElementById("rmgForm");

  const titleEl = document.getElementById("rmgTitle");
  const reportedAtEl = document.getElementById("rmgReportedAt");
  const authorEl = document.getElementById("rmgAuthor");
  const reporterEl = document.getElementById("rmgReporter");
  const reasonEl = document.getElementById("rmgReason");
  const reasonExtraEl = document.getElementById("rmgReasonExtra");
  const contentEl = document.getElementById("rmgContent");
  const statusEl = document.getElementById("rmgStatus");
  const applyBtn = document.getElementById("rmgApplyBtn");

  let currentId = null;
  let original = null;

  // 예시 데이터
  let rows = [
    {
      id: 1,
      postId: 5,
      title: "테스트 글 A",
      author: "ssar",
      reporter: "cos",
      reason: "홍보글",
      reasonExtra: "",
      content: "...",
      status: "처리 전",
      date: "2025-08-07",
    },
    {
      id: 2,
      postId: 5,
      title: "테스트 글 B",
      author: "ssar",
      reporter: "cos",
      reason: "기타",
      reasonExtra: "추가 설명",
      content: "...",
      status: "처리 중",
      date: "2025-08-07",
    },
    {
      id: 3,
      postId: 5,
      title: "테스트 글 C",
      author: "ssar",
      reporter: "cos",
      reason: "부적절한 내용",
      reasonExtra: "",
      content: "...",
      status: "처리 완료",
      date: "2025-08-07",
    },
    {
      id: 4,
      postId: 5,
      title: "테스트 글 A",
      author: "ssar",
      reporter: "cos",
      reason: "홍보글",
      reasonExtra: "",
      content: "...",
      status: "처리 전",
      date: "2025-08-07",
    },
    {
      id: 5,
      postId: 5,
      title: "테스트 글 A",
      author: "ssar",
      reporter: "cos",
      reason: "홍보글",
      reasonExtra: "",
      content: "...",
      status: "처리 전",
      date: "2025-08-07",
    },
    {
      id: 6,
      postId: 5,
      title: "테스트 글 A",
      author: "ssar",
      reporter: "cos",
      reason: "홍보글",
      reasonExtra: "",
      content: "...",
      status: "처리 전",
      date: "2025-08-07",
    },
    {
      id: 7,
      postId: 5,
      title: "테스트 글 A",
      author: "ssar",
      reporter: "cos",
      reason: "홍보글",
      reasonExtra: "",
      content: "...",
      status: "처리 전",
      date: "2025-08-07",
    },
    {
      id: 8,
      postId: 5,
      title: "테스트 글 A",
      author: "ssar",
      reporter: "cos",
      reason: "홍보글",
      reasonExtra: "",
      content: "...",
      status: "처리 전",
      date: "2025-08-07",
    },
    {
      id: 9,
      postId: 5,
      title: "테스트 글 A",
      author: "ssar",
      reporter: "cos",
      reason: "홍보글",
      reasonExtra: "",
      content: "...",
      status: "처리 전",
      date: "2025-08-07",
    },
    {
      id: 10,
      postId: 5,
      title: "테스트 글 A",
      author: "ssar",
      reporter: "cos",
      reason: "홍보글",
      reasonExtra: "",
      content: "...",
      status: "처리 전",
      date: "2025-08-07",
    },
    {
      id: 11,
      postId: 5,
      title: "테스트 글 A",
      author: "ssar",
      reporter: "cos",
      reason: "홍보글",
      reasonExtra: "",
      content: "...",
      status: "처리 전",
      date: "2025-08-07",
    },
    {
      id: 12,
      postId: 5,
      title: "테스트 글 A",
      author: "ssar",
      reporter: "cos",
      reason: "홍보글",
      reasonExtra: "",
      content: "...",
      status: "처리 전",
      date: "2025-08-07",
    },
  ];

  function statusClass(s) {
    return s === "처리 완료"
      ? "rmg-status rmg-status--d"
      : s === "처리 중"
      ? "rmg-status rmg-status--p"
      : "rmg-status rmg-status--w";
  }

  function render() {
    tbody.innerHTML = rows
      .map(
        (r) => `
        <tr data-id="${r.id}" tabindex="0">
          <td>${r.id}</td>
          <td>${r.postId}</td>
          <td title="${esc(r.title)}">${esc(r.title)}</td>
          <td>${esc(r.author)}</td>
          <td>${esc(r.reporter)}</td>
          <td>${esc(r.reason)}</td>
          <td><span class="${statusClass(r.status)}">${esc(
          r.status
        )}</span></td>
          <td>${esc(r.date)}</td>
        </tr>
      `
      )
      .join("");
  }
  render();

  // 행 클릭/키보드로 모달 열기
  tbody.addEventListener("click", openFromEvent);
  tbody.addEventListener("keydown", (e) => {
    if (e.key === "Enter" || e.key === " ") {
      e.preventDefault();
      openFromEvent(e);
    }
  });
  function openFromEvent(e) {
    const tr = e.target.closest("tr");
    if (!tr) return;
    openModal(Number(tr.dataset.id));
  }

  function openModal(id) {
    const row = rows.find((r) => r.id === id);
    if (!row) return;
    currentId = id;

    // 값 바인딩
    titleEl.value = row.title || "";
    reportedAtEl.value = row.date || "";
    authorEl.value = row.author || "";
    reporterEl.value = row.reporter || "";
    ensureOption(reasonEl, row.reason);
    reasonEl.value = row.reason || "기타";
    reasonExtraEl.value = row.reasonExtra || "";
    contentEl.value = row.content || "";
    ensureOption(statusEl, row.status);
    statusEl.value = row.status || "처리 전";

    // ===== 읽기전용 스타일 설정 (disabled 없이) =====
    [
      titleEl,
      reportedAtEl,
      authorEl,
      reporterEl,
      contentEl,
      reasonExtraEl,
    ].forEach((el) => {
      el.readOnly = true; // 입력 불가
      el.classList.add("rmg-readonly"); // 스타일 고정 (select 제외)
      // input/textarea엔 rmg-readonly 없어도 [readonly] 스타일이 먹지만 일관성 위해 추가
    });

    // select(신고 사유)는 readonly가 없으므로 전용 클래스로 차단
    reasonEl.setAttribute("aria-readonly", "true");
    reasonEl.classList.add("rmg-readonly");

    // 기타 사유 강조/비활성 색상
    if (reasonEl.value === "기타") {
      reasonExtraEl.classList.remove("rmg-disabled");
    } else {
      reasonExtraEl.classList.add("rmg-disabled");
    }

    // 상태만 변경 가능 → 읽기전용 제거
    statusEl.classList.remove("rmg-readonly");
    statusEl.removeAttribute("aria-readonly");

    original = { status: statusEl.value };
    applyBtn.disabled = true;

    show(modal);
    focusTrap(modal);
  }

  // 상태 변경 시에만 저장 활성화
  statusEl.addEventListener("change", () => {
    applyBtn.disabled = statusEl.value === original.status;
  });

  // 닫기
  document.querySelectorAll('[data-close="true"]').forEach((btn) => {
    btn.addEventListener("click", () => hide(modal));
  });
  modal.addEventListener("click", (e) => {
    if (e.target.classList.contains("rmg-modal__backdrop")) hide(modal);
  });
  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape" && !modal.classList.contains("is-hidden"))
      hide(modal);
  });

  // 적용(상태만 저장)
  form.addEventListener("submit", (e) => {
    e.preventDefault();
    if (applyBtn.disabled || currentId == null) return;
    const idx = rows.findIndex((r) => r.id === currentId);
    if (idx >= 0) {
      rows[idx].status = statusEl.value;
      render();
    }
    hide(modal);
  });

  /* helpers */
  function ensureOption(select, value) {
    if (!value) return;
    const ok = Array.from(select.options).some((o) => o.value === value);
    if (!ok) {
      const o = document.createElement("option");
      o.value = value;
      o.textContent = value;
      select.appendChild(o);
    }
  }
  function show(m) {
    m.classList.remove("is-hidden");
  }
  function hide(m) {
    m.classList.add("is-hidden");
  }
  function esc(s) {
    return String(s)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#39;");
  }

  function focusTrap(modal) {
    const f = modal.querySelectorAll(
      'button,[href],input,select,textarea,[tabindex]:not([tabindex="-1"])'
    );
    if (!f.length) return;
    const first = f[0],
      last = f[f.length - 1];
    setTimeout(() => first.focus(), 0);
    function handle(e) {
      if (e.key !== "Tab") return;
      if (e.shiftKey && document.activeElement === first) {
        e.preventDefault();
        last.focus();
      } else if (!e.shiftKey && document.activeElement === last) {
        e.preventDefault();
        first.focus();
      }
    }
    modal.addEventListener("keydown", handle, { once: true });
  }
});
