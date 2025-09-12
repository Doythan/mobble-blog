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
  const dataEl = document.getElementById("reports-data");
  let rows = JSON.parse(dataEl.dataset.json);
  console.log(rows);

  function escapeHtml(s) {
    return String(s)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");
  }
  /* ---------- reason,status 매핑 ---------- */
  const REASON_MAP = {
    INAPPROPRIATE_AUTHOR_NAME: "부적절한 작성자명",
    INAPPROPRIATE_BOARD_CONTENT: "부적절한 글 내용",
    ADVERTISING_BOARD_CONTENT: "광고성 글 내용",
    COPYRIGHT_VIOLATION: "저작권 침해",
    PERSONAL_INFORMATION: "개인정보 노출",
    ABUSIVE_LANGUAGE: "욕설/비방",
    SPAM: "도배/스팸",
    ETC: "기타",
  };

  const STATUS_LABELS = {
    PENDING: "처리 전",
    PROCESSING: "처리 중",
    COMPLETED: "처리 완료",
  };

  function statusClass(s) {
    return s === "COMPLETED"
      ? "rmg-status rmg-status--d"
      : s === "PROCESSING"
      ? "rmg-status rmg-status--p"
      : "rmg-status rmg-status--w";
  }

  function pickReasonText(r) {
    const raw = r.result ?? "";
    return REASON_MAP[raw] ?? raw;
  }

  function setReasonBack(r, newReasonText) {
    const code =
        Object.entries(REASON_MAP).find(
            ([, label]) => label === newReasonText
        )?.[0] ?? newReasonText;

    r.result = code;
    return code;// 핵심 필드 하나만 갱신
  }

  function render() {
    tbody.innerHTML = rows
        .map((r) => {
          const reasonTxt = pickReasonText(r); // Enum → 한글 변환
          const statusCode = r.status ?? "PENDING"; // 서버 ENUM 값
          const statusText = STATUS_LABELS[statusCode] ?? statusCode; // 한글 변환

          return `
        <tr data-id="${r.id}" tabindex="0">
          <td>${r.id}</td>
          <td>${r.boardId}</td>
          <td title="${escapeHtml(r.boardTitle ?? "")}">
            ${escapeHtml(r.boardTitle ?? "")}
          </td>
          <td>${escapeHtml(r.reportedUsername ?? "-")}</td>
          <td>${escapeHtml(r.reportingUsername ?? "-")}</td>
          <td title="${escapeHtml(reasonTxt)}">
            ${escapeHtml(reasonTxt || "-")}
          </td>
          <td>
            <span class="${statusClass(statusCode)}">
              ${escapeHtml(statusText)}
            </span>
          </td>
          <td>
            ${r.createdAt ? new Date(r.createdAt).toLocaleDateString() : "-"}
          </td>
        </tr>
      `;
        })
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
    console.log(row.boardTitle);
    console.log(row.result);

    // 값 바인딩
    titleEl.value = row.boardTitle || "";
    reportedAtEl.value = row.createdAt
        ? new Date(row.createdAt).toISOString().split("T")[0]
        : "";
    authorEl.value = row.reportedUsername || "";
    reporterEl.value = row.reportingUsername || "";

    ensureOption(reasonEl, row.result, "reason");
    reasonEl.value = row.result || "ETC";   // <-- 영문 ENUM 넣기
    reasonExtraEl.value = row.resultEtc || "";
    contentEl.value = row.content || "";
    ensureOption(statusEl, row.status, "status")
    statusEl.value = row.status|| "처리 전";

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
    if (reasonEl.value === "ETC") {
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
  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    if (applyBtn.disabled || currentId == null) return;

    const idx = rows.findIndex((r) => r.id === currentId);
    if (idx < 0) return;

    const newStatus = statusEl.value;

    try {
      // ✅ REST API 호출 (예: /admin/reports/{id}/status)
      const res = await fetch(`/admin/reports/${currentId}/update`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ status: newStatus }),
      });

      if (!res.ok) {
        throw new Error("서버 상태 업데이트 실패");
      }
      const updated = await res.json();
      console.log("updated : "+updated)
      // 성공 시 프론트 데이터 갱신
      rows[idx].status = newStatus;
      render();
      hide(modal);
    } catch (err) {
      alert("상태 저장 중 오류: " + err.message);
    }
  });

  //enum 한글 변경 로직
  function ensureOption(select, value, type = "reason") {
    if (!value) return;

    const map = type === "status" ? STATUS_LABELS : REASON_MAP;
    const label = map[value] ?? value;

    // 같은 value가 없으면 새로 추가
    const option = Array.from(select.options).find((o) => o.value === value);
    if (!option) {
      const o = document.createElement("option");
      o.value = value;      // ENUM 그대로
      o.textContent = label; // 화면에 보일 한글
      select.appendChild(o);
    } else {
      // 이미 있으면 label을 최신 한글로 보장
      option.textContent = label;
    }
  }

  function show(m) {
    m.classList.remove("is-hidden");
  }
  function hide(m) {
    m.classList.add("is-hidden");
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
