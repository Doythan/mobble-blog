(function () {
    // 데이터 파싱
    const dataEl = document.getElementById('users-data');
    let users = [];
    try {
        users = JSON.parse(dataEl?.dataset?.json || '[]');
    } catch (e) {
        console.error('usersJson 파싱 실패', e);
    }

    const tbody = document.getElementById('umgTbody');
    const modal = document.getElementById('umgModal');
    const form = document.getElementById('umgForm');

    const $ = (sel) => document.querySelector(sel);
    const $id = (id) => document.getElementById(id);

    const $userId = $id('umgUserId');
    const $username = $id('umgUsername');
    const $email = $id('umgEmail');
    const $role = $id('umgRole');
    const $status = $id('umgStatus');
    const $applyBtn = $id('umgApplyBtn');
    const $deleteBtn = $id('umgDeleteBtn');

    function rowTpl(u) {
        const cls = u.status === 'DELETED' ? 'is-muted' : '';
        return `
      <tr class="${cls}" data-id="${u.id}">
        <td>${u.id}</td>
        <td>${escapeHtml(u.username)}</td>
        <td>${escapeHtml(u.email || '')}</td>
        <td>${escapeHtml(u.role || '')}</td>
        <td>${escapeHtml(u.status || '')}</td>
      </tr>
    `;
    }

    function render() {
        if (!Array.isArray(users) || users.length === 0) {
            tbody.innerHTML = `<tr><td colspan="5" class="empty">데이터가 없습니다.</td></tr>`;
            return;
        }
        tbody.innerHTML = users.map(rowTpl).join('');
    }

    function escapeHtml(s) {
        return String(s ?? '')
            .replaceAll('&', '&amp;')
            .replaceAll('<', '&lt;')
            .replaceAll('>', '&gt;')
            .replaceAll('"', '&quot;')
            .replaceAll("'", '&#39;');
    }

    function openModal(u) {
        $userId.value = u.id;
        $username.value = u.username ?? '';
        $email.value = u.email ?? '';
        $role.value = u.role ?? '';
        $status.value = u.status ?? '';

        // 상태가 DELETED면 조작 방지
        const deleted = u.status === 'DELETED';
        $username.disabled = deleted;
        $applyBtn.disabled = true; // 변경 감지 전까지 비활성
        $deleteBtn.disabled = deleted; // 이미 삭제된 경우 비활성

        modal.classList.remove('is-hidden');
        $username.focus();
    }

    function closeModal() {
        modal.classList.add('is-hidden');
    }

    // 목록 클릭 → 모달 열기
    tbody.addEventListener('click', (e) => {
        const tr = e.target.closest('tr');
        if (!tr) return;
        const id = Number(tr.dataset.id);
        const u = users.find((x) => x.id === id);
        if (!u) return;
        openModal(u);
    });

    // 모달 닫기
    modal.addEventListener('click', (e) => {
        if (e.target.dataset.close === 'true') closeModal();
    });

    // 입력 변경 → 적용 버튼 활성화
    $username.addEventListener('input', () => {
        const id = Number($userId.value);
        const original = users.find((x) => x.id === id);
        const changed = ($username.value || '').trim() !== (original?.username || '');
        $applyBtn.disabled = !changed;
    });

    // 닉네임 적용
    form.addEventListener('submit', async () => {
        const id = Number($userId.value);
        const nickname = ($username.value || '').trim();
        if (!nickname) return alert('닉네임을 입력해주세요.');
        if (nickname.length > 20) return alert('닉네임은 20자 이하여야 합니다.');

        try {
            await fetch(`/admin/users/${id}/update`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ nickname })
            }).then(checkOk);

            // 로컬 데이터 & 테이블 반영
            const u = users.find((x) => x.id === id);
            if (u) u.username = nickname;
            render();
            closeModal();
            alert('닉네임이 변경되었습니다.');
        } catch (err) {
            console.error(err);
            alert(extractMsg(err) || '닉네임 변경에 실패했습니다.');
        }
    });

    // 강제 탈퇴
    $deleteBtn.addEventListener('click', async () => {
        const id = Number($userId.value);
        if (!confirm('정말로 강제 탈퇴 처리하시겠습니까?')) return;

        try {
            await fetch(`/admin/users/${id}/delete`, { method: 'POST' }).then(checkOk);

            // 소프트 삭제: status만 DELETED로 표시
            const u = users.find((x) => x.id === id);
            if (u) u.status = 'DELETED';
            render();
            closeModal();
            alert('탈퇴 처리되었습니다.');
        } catch (err) {
            console.error(err);
            alert(extractMsg(err) || '탈퇴 처리에 실패했습니다.');
        }
    });

    function checkOk(res) {
        if (!res.ok) return res.text().then((t) => { throw new Error(t || res.statusText); });
        return res;
    }
    function extractMsg(err) {
        const t = String(err?.message || '');
        try {
            if (t.startsWith('{')) return JSON.parse(t).message;
        } catch {}
        return t;
    }

    // 초기 렌더
    render();
})();
