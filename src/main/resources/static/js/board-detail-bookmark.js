document.addEventListener("DOMContentLoaded", () => {
  const btn = document.querySelector(".btn-bookmark");
  if (!btn) return;

  const path = btn.querySelector("path");
  const boardId = Number(btn.dataset.boardId || 0);
  const createUrl = btn.dataset.createUrl; // POST
  const deleteUrl = btn.dataset.deleteUrl; // DELETE

  // UI 상태 반영 함수
  function paint(bookmarked) {
    btn.dataset.bookmarked = String(bookmarked);
    btn.setAttribute("aria-pressed", String(bookmarked));
    if (bookmarked) {
      // 노란 내부 + 검정 테두리
      path.setAttribute("fill", "gold");
      path.setAttribute("stroke", "#000");
      path.setAttribute("stroke-width", "1.5");
    } else {
      // 비어 있고 검정 테두리
      path.setAttribute("fill", "none");
      path.setAttribute("stroke", "currentColor");
      path.setAttribute("stroke-width", "1.5");
    }
  }

  // 초기 렌더 반영(서버가 내려준 data-bookmarked에 맞춤)
  paint(btn.dataset.bookmarked === "true");

  // 클릭 시 토글
  btn.addEventListener("click", async () => {
    const isBookmarked = btn.dataset.bookmarked === "true";

    try {
      let resp;
      if (isBookmarked) {
        // 삭제
        resp = await fetch(deleteUrl, {
          method: "DELETE",
          headers: { Accept: "application/json" },
        });
      } else {
        // 추가
        resp = await fetch(createUrl, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Accept: "application/json",
          },
          body: JSON.stringify({ boardId }), // 서버 DTO에 맞게 필요 시 필드명 조정
        });
      }

      // 통신 결과 파싱
      const data = await resp.json().catch(() => ({}));

      // 인증/권한 처리 (원한다면 401/403 별도 처리)
      if (resp.status === 401) {
        alert("로그인이 필요합니다.");
        return;
      }
      if (!resp.ok || data.status !== 200) {
        throw new Error(data.msg || "요청 실패");
      }

      // JSON 규약에 맞춰 UI 반영
      // - 추가 성공: { status:200, body:{...} } → body 존재
      // - 삭제 성공: { status:200, body:null }
      const nowBookmarked = !!data.body;
      paint(nowBookmarked);
    } catch (err) {
      console.error(err);
      alert("북마크 처리에 실패했습니다. 잠시 후 다시 시도해 주세요.");
    }
  });
});
