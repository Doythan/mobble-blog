document.addEventListener("DOMContentLoaded", () => {
  // 아바타 미리보기
  const input = document.getElementById("avatarInput");
  const preview = document.getElementById("avatarPreview");
  if (input && preview) {
    input.addEventListener("change", () => {
      const file = input.files?.[0];
      if (!file) return;
      const url = URL.createObjectURL(file);
      preview.src = url;
    });
  }

  // 공통 유틸
  const lockScroll = () => (document.body.style.overflow = "hidden");
  const unlockScroll = () => (document.body.style.overflow = "");

  function openModal(modal, focusEl) {
    if (!modal) return;
    modal.style.display = "block"; // ★ 인라인 display 켜기
    modal.setAttribute("aria-hidden", "false");
    lockScroll();
    if (focusEl) setTimeout(() => focusEl.focus(), 0);
  }
  function closeModal(modal) {
    if (!modal) return;
    modal.setAttribute("aria-hidden", "true");
    modal.style.display = "none"; // ★ 인라인 display 끄기
    unlockScroll();
  }

  /* ===== 비밀번호 재설정 모달 ===== */
  const pwdModal = document.getElementById("pwdModal");
  const pwdBackdrop = document.getElementById("pwdBackdrop");
  const openPwd = document.getElementById("openPwdModal");
  const closePwdBtns = document.querySelectorAll('[data-close="pwd"]');
  const pwdForm = document.getElementById("pwdForm");
  const newPwd = document.getElementById("newPwd");
  const newPwd2 = document.getElementById("newPwd2");

  openPwd?.addEventListener("click", () => openModal(pwdModal, newPwd));
  pwdBackdrop?.addEventListener("click", () => closeModal(pwdModal));
  closePwdBtns.forEach((btn) =>
    btn.addEventListener("click", () => closeModal(pwdModal))
  );
  window.addEventListener("keydown", (e) => {
    if (
      e.key === "Escape" &&
      pwdModal?.getAttribute("aria-hidden") === "false"
    ) {
      closeModal(pwdModal);
    }
  });

  // 간단 검증
  pwdForm?.addEventListener("submit", (e) => {
    if (!newPwd.value.trim() || !newPwd2.value.trim()) {
      e.preventDefault();
      alert("비밀번호를 입력해 주세요.");
      return;
    }
    if (newPwd.value !== newPwd2.value) {
      e.preventDefault();
      alert("비밀번호가 일치하지 않습니다.");
      newPwd2.focus();
      return;
    }
    // 기본 submit 진행
  });

  /* ===== 로그아웃 모달 ===== */
  const logoutModal = document.getElementById("logoutModal");
  const logoutBackdrop = document.getElementById("logoutBackdrop");
  const openLogout = document.getElementById("openLogoutModal");
  const closeLogoutBtns = document.querySelectorAll('[data-close="logout"]');

  openLogout?.addEventListener("click", () => openModal(logoutModal));
  logoutBackdrop?.addEventListener("click", () => closeModal(logoutModal));
  closeLogoutBtns.forEach((btn) =>
    btn.addEventListener("click", () => closeModal(logoutModal))
  );
  window.addEventListener("keydown", (e) => {
    if (
      e.key === "Escape" &&
      logoutModal?.getAttribute("aria-hidden") === "false"
    ) {
      closeModal(logoutModal);
    }
  });
});
