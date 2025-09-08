document.addEventListener("DOMContentLoaded", () => {
  const $modal = document.getElementById("reportModal");
  const $backdrop = document.getElementById("reportBackdrop");
  const $form = document.getElementById("reportForm");
  const $reason = document.getElementById("reportReason");
  const $reasonEtc = document.getElementById("reportReasonEtc");
  const $content = document.getElementById("reportContent");
  const $cancels = document.querySelectorAll(".report-btn--cancel");

  if (!$modal) return;

  const lockScroll = () => {
    document.body.style.overflow = "hidden";
  };
  const unlockScroll = () => {
    document.body.style.overflow = "";
  };

  function openModal() {
    $modal.style.display = "block";
    $modal.classList.add("is-open"); // 상태 플래그(스타일 의존 X)
    $modal.setAttribute("aria-hidden", "false");
    lockScroll();
    setTimeout(() => $reason?.focus(), 0);
  }

  function closeModal() {
    $modal.classList.remove("is-open");
    $modal.setAttribute("aria-hidden", "true");
    $modal.style.display = "none";
    unlockScroll();
  }

  // 열기: 메뉴/버튼 트리거 (둘 다 지원)
  document.addEventListener("click", (e) => {
    const trigger = e.target.closest(
      '#openReportModal, [data-open="report-modal"]'
    );
    if (trigger) {
      e.preventDefault();
      openModal();
    }
  });

  // 닫기: 취소 / 배경 / ESC
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

  // ‘기타’ 선택 시 입력 활성화
  function syncEtcField() {
    if (!$reason || !$reasonEtc) return;
    const on = $reason.value === "ETC";
    $reasonEtc.disabled = !on;
    if (!on) $reasonEtc.value = "";
  }
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
    // 기본 form submit 진행 (페이지 이동)
  });
});
