document.addEventListener("DOMContentLoaded", () => {
  const $modal = document.getElementById("reportModal");
  const $dialog = $modal?.querySelector(".report-modal__dialog");
  const $backdrop = document.getElementById("reportBackdrop");
  const $form = document.getElementById("reportForm");
  const $reason = document.getElementById("reportReason");
  const $reasonEtc = document.getElementById("reportReasonEtc");
  const $content = document.getElementById("reportContent");
  const $cancels = document.querySelectorAll(".report-btn--cancel");

  if (!$modal || !$dialog) return;

  let isClosing = false;

  const lockScroll = () => {
    document.body.style.overflow = "hidden";
  };
  const unlockScroll = () => {
    document.body.style.overflow = "";
  };

  /** 열기: display → 강제 리플로우 → is-open 추가(트랜지션 시작) */
  function openModal() {
    if ($modal.style.display === "block") return;

    // 1) 보이기
    $modal.style.display = "block";
    $modal.setAttribute("aria-hidden", "false");

    // 2) 초기 상태를 확실히 적용시키기 위해 강제 리플로우
    //   - 이 줄이 없으면 브라우저가 최적화해서 트랜지션을 스킵할 수 있음
    void $dialog.offsetWidth; // 강제 reflow

    // 3) 트랜지션 시작
    $modal.classList.add("is-open");
    lockScroll();

    // 포커스 이동
    setTimeout(() => $reason?.focus(), 0);
  }

  /** 닫기: is-open 제거(트랜지션 시작) → transitionend 후 display:none */
  function closeModal() {
    if (isClosing || $modal.style.display !== "block") return;
    isClosing = true;

    $modal.classList.remove("is-open");
    $modal.setAttribute("aria-hidden", "true");

    const done = () => {
      $modal.style.display = "none";
      unlockScroll();
      isClosing = false;
      $dialog.removeEventListener("transitionend", onEnd);
      $backdrop?.removeEventListener("transitionend", onEnd);
    };
    const onEnd = (e) => {
      // dialog 또는 backdrop 중 하나라도 트랜지션이 끝나면 닫기 완료
      if (e.target === $dialog || e.target === $backdrop) done();
    };

    // 트랜지션 종료 감지
    $dialog.addEventListener("transitionend", onEnd);
    $backdrop?.addEventListener("transitionend", onEnd);

    // 혹시 transitionend를 못 받는 예외를 위한 안전 타임아웃 (CSS 200ms 기준 + 여유)
    setTimeout(done, 300);
  }

  /** ‘기타’ 선택 시 텍스트박스 활성화/비활성 동기화 */
  function syncEtcField() {
    if (!$reason || !$reasonEtc) return;
    const isEtc = $reason.value === "ETC";
    $reasonEtc.disabled = !isEtc;
    if (!isEtc) $reasonEtc.value = "";
  }

  // ===== 이벤트 바인딩 =====

  // 열기 트리거(메뉴 등): #openReportModal 또는 data-open="report-modal"
  document.addEventListener("click", (e) => {
    const trigger = e.target.closest(
      '#openReportModal, [data-open="report-modal"]'
    );
    if (trigger) {
      e.preventDefault();
      openModal();
    }
  });

  // 닫기: 취소 버튼 / 배경 클릭 / ESC
  $cancels.forEach((btn) =>
    btn.addEventListener("click", (e) => {
      e.preventDefault();
      closeModal();
    })
  );
  $backdrop?.addEventListener("click", closeModal);
  window.addEventListener("keydown", (e) => {
    if (e.key === "Escape" && $modal.classList.contains("is-open"))
      closeModal();
  });

  // 기타 필드 동기화
  $reason?.addEventListener("change", syncEtcField);
  syncEtcField();

  // 제출 검증
  $form?.addEventListener("submit", (e) => {
    if (!$content?.value.trim()) {
      e.preventDefault();
      alert("신고 내용을 입력해 주세요.");
      $content?.focus();
      return;
    }
    if ($reason?.value === "ETC" && !$reasonEtc?.value.trim()) {
      e.preventDefault();
      alert("기타 사유를 입력해 주세요.");
      $reasonEtc?.focus();
      return;
    }
    // 기본 submit으로 서버 전송 (페이지 이동 발생)
    // AJAX로 처리할 경우 성공 콜백에서 closeModal() 호출하세요.
  });
});
