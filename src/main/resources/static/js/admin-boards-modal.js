window.addEventListener("load", () => {
  const tbody = document.getElementById("rmgTbody");
  const modal = document.getElementById("rmgModal");

  const titleEl = document.getElementById("rmgTitle");
  const reportedAtEl = document.getElementById("rmgReportedAt");
  const authorEl = document.getElementById("rmgAuthor");
  const contentEl = document.getElementById("rmgContent");
  const categoryEl = document.getElementById("rmgCategory");
  const idEl = document.getElementById("rmgId");
  const viewsEl = document.getElementById("rmgViews");
  const deleteBtn = document.getElementById("rmgDeleteBtn");
  let currentId = null;

  // 데이터 가져오기
  const dataEl = document.getElementById("reports-data");
  let rows = [];
  try {
    rows = JSON.parse(dataEl.dataset.json || "[]");
  } catch (e) {
    console.error("reportsJson 파싱 실패:", e);
  }
  console.log("들어온 데이터:", rows);

  function escapeHtml(s) {
    return String(s)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");
  }

  function truncateText(str, maxLength = 45) {
    if (!str) return "-";
    return str.length > maxLength ? str.slice(0, maxLength) + "..." : str;
  }

  function render() {
    if (!tbody) return;
    tbody.innerHTML = rows
        .map((r) => `
        <tr data-id="${r.id}" tabindex="0">
          <td>${r.id}</td>
          <td title="${escapeHtml(r.title ?? "")}">${escapeHtml(r.title ?? "")}</td>
          <td>${escapeHtml(r.username ?? "-")}</td>
          <td title="${escapeHtml(r.content ?? "")}">${escapeHtml(truncateText(r.content, 45))}</td>
          <td>${r.createdAt ? new Date(r.createdAt).toLocaleDateString() : "-"}</td>
        </tr>
      `).join("");
  }
  render();

  // 행 클릭/키보드로 모달 열기
  if (tbody) {
    tbody.addEventListener("click", openFromEvent);
    tbody.addEventListener("keydown", (e) => {
      if (e.key === "Enter" || e.key === " ") {
        e.preventDefault();
        openFromEvent(e);
      }
    });
  }

  function openFromEvent(e) {
    const tr = e.target.closest("tr");
    if (!tr) return;
    openModal(Number(tr.dataset.id));
  }

  function openModal(id) {
    const row = rows.find((r) => r.id === id);
    if (!row) return;
    currentId = id;

    if (titleEl) titleEl.textContent = row.title || "";
    if (reportedAtEl) reportedAtEl.textContent = row.createdAt ? new Date(row.createdAt).toISOString().split("T")[0] : "";
    if (authorEl) authorEl.textContent = row.username || "";
    if (contentEl) contentEl.textContent = row.content || "";
    if (categoryEl) categoryEl.textContent = row.category || "";
    if (idEl) idEl.textContent = row.id ?? "";
    if (viewsEl) viewsEl.textContent = row.views ?? "0";

    // 읽기전용 스타일 적용
    [contentEl].forEach(el => {
      if (!el) return;
      el.readOnly = true;
      el.classList.add("rmg-readonly");
    });

    show(modal);
    focusTrap(modal);
  }

  // 닫기
  document.querySelectorAll('[data-close="true"]').forEach((btn) => {
    btn.addEventListener("click", () => hide(modal));
  });
  if (modal) {
    modal.addEventListener("click", (e) => {
      if (e.target.classList.contains("rmg-modal__backdrop")) hide(modal);
    });
  }
  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape" && modal && !modal.classList.contains("is-hidden"))
      hide(modal);
  });

  // 삭제 버튼
  if (deleteBtn) {
    deleteBtn.addEventListener("click", async () => {
      if (currentId == null) return;
      if (!confirm("정말 게시글을 삭제하시겠습니까?")) return;

      try {
        const res = await fetch(`/admin/boards/${currentId}/delete`, { method: "POST" });
        if (!res.ok) throw new Error("게시글 삭제 실패");

        rows = rows.filter(r => r.id !== currentId);
        render();
        hide(modal);
        alert("게시글이 삭제되었습니다.");
      } catch (err) {
        alert("삭제 중 오류: " + err.message);
      }
    });
  }

  // 모달 표시/숨기기
  function show(m) { if(m) m.classList.remove("is-hidden"); }
  function hide(m) { if(m) m.classList.add("is-hidden"); }

  // 포커스 트랩
  function focusTrap(modal) {
    if (!modal) return;
    const f = modal.querySelectorAll('button,[href],input,select,textarea,[tabindex]:not([tabindex="-1"])');
    if (!f.length) return;
    const first = f[0], last = f[f.length - 1];
    setTimeout(() => first.focus(), 0);

    function handle(e) {
      if (e.key !== "Tab") return;
      if (e.shiftKey && document.activeElement === first) {
        e.preventDefault(); last.focus();
      } else if (!e.shiftKey && document.activeElement === last) {
        e.preventDefault(); first.focus();
      }
    }
    modal.addEventListener("keydown", handle, { once: true });
  }
});