document.addEventListener("DOMContentLoaded", () => {
    const input = document.getElementById("profileImage");
    const preview = document.getElementById("avatarPreview");
    const label = document.querySelector("label.mp-avatar");
    const help = document.querySelector(".mp-help");
    const userId = document.getElementById("userId")?.value;

    function setBusy(busy) {
        if (!label) return;
        label.classList.toggle("is-uploading", busy);
        if (help) help.textContent = busy ? "업로드 중..." : "클릭하여 이미지를 변경할 수 있습니다.";
        if (input) input.disabled = busy;
    }

    input?.addEventListener("change", async () => {
        const file = input.files?.[0];
        if (!file) return;

        // 가벼운 클라이언트 검증(선택)
        if (!file.type.startsWith("image/")) {
            alert("이미지 파일만 업로드할 수 있습니다.");
            input.value = "";
            return;
        }
        if (file.size > 5 * 1024 * 1024) { // 5MB 예시
            alert("파일이 너무 큽니다. 5MB 이하로 업로드해주세요.");
            input.value = "";
            return;
        }

        // 미리보기(낙관적 UI)
        const revokeUrl = preview.src?.startsWith("blob:") ? preview.src : null;
        const blobUrl = URL.createObjectURL(file);
        preview.src = blobUrl;
        if (revokeUrl) URL.revokeObjectURL(revokeUrl);

        // 업로드
        const formData = new FormData();
        formData.append("profileImage", file); // 서버 파라미터명: avatar

        setBusy(true);
        try {
            const res = await fetch(`/users/${userId}/profile`, {
                method: "POST",
                body: formData,
                // headers: {
                //   ...(csrfHeaderName && csrfToken ? { [csrfHeaderName]: csrfToken } : {}),
                //   ...(jwt ? { "Authorization": `Bearer ${jwt}` } : {})
                // },
                // credentials: "include" // 세션/쿠키 인증을 쓰면 활성화
            });

            if (!res.ok) {
                // 업로드 실패 시 이전 이미지로 롤백하고 에러 메시지 표시
                alert("업로드에 실패했습니다.");
                input.value = "";
                // 서버가 실패했더라도 당장 롤백할 이전 URL을 모르면 기본 이미지로 되돌릴 수도 있음
                // preview.src = "/img/default_profile.png";
                return;
            }

            // 서버가 최종 저장 경로를 응답(JSON)으로 내려준다면, 그 URL로 갱신
            // 예: { "url": "/uploads/avatars/123.png" }
            const data = await res.json().catch(() => ({}));
            if (data?.url) {
                preview.src = data.url; // 캐시 무효화를 원하면 `?t=${Date.now()}` 추가
            }
            help && (help.textContent = "업로드 완료!");
        } catch (e) {
            alert("네트워크 오류로 업로드에 실패했습니다.");
            input.value = "";
            // preview.src = "/img/default_profile.png";
        } finally {
            setBusy(false);
        }
    });

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
