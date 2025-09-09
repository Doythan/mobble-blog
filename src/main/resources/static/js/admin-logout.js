document.addEventListener("DOMContentLoaded", () => {
  const logoutBtn = document.querySelector(".rmg-logout-btn");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", () => {
      // TODO: 실제 로그아웃 처리
      alert("로그아웃 되었습니다.");
      window.location.href = "/admin/login";
    });
  }
});
