(function () {
    const fab = document.getElementById('aiFab');
    const panel = document.getElementById('aiPanel');
    const closeBtn = document.getElementById('aiClose');
    const resetBtn = document.getElementById('aiReset');
    const form = document.getElementById('aiForm');
    const text = document.getElementById('aiText');
    const send = document.getElementById('aiSend');
    const messages = document.getElementById('aiMessages');
    const statusEl = document.getElementById('aiStatus');

    if (!fab || !panel) return;

    function openPanel() {
        panel.classList.add('open');
        panel.setAttribute('aria-hidden', 'false');
        setTimeout(() => text && text.focus(), 100);
    }

    function closePanel() {
        panel.classList.remove('open');
        panel.setAttribute('aria-hidden', 'true');
    }

    fab.addEventListener('click', () => {
        if (panel.classList.contains('open')) closePanel();
        else openPanel();
    });
    closeBtn && closeBtn.addEventListener('click', closePanel);

    // Luk med Escape
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && panel.classList.contains('open')) closePanel();
    });

    // Auto resize textarea
    if (text) {
        text.addEventListener('input', () => {
            text.style.height = 'auto';
            text.style.height = Math.min(text.scrollHeight, 120) + 'px';
        });
        // Enter sender, Shift+Enter laver linjeskift
        text.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                form.requestSubmit();
            }
        });
    }

    function addMessage(role, content, extraClass) {
        const msg = document.createElement('div');
        msg.className = 'ai-msg ai-msg-' + role + (extraClass ? ' ' + extraClass : '');
        const inner = document.createElement('div');
        inner.className = 'ai-msg-content';
        if (extraClass === 'loading') {
            inner.innerHTML = '<span></span><span></span><span></span>';
        } else {
            inner.textContent = content;
        }
        msg.appendChild(inner);
        messages.appendChild(msg);
        messages.scrollTop = messages.scrollHeight;
        return msg;
    }

    resetBtn && resetBtn.addEventListener('click', async () => {
        try {
            await fetch('/ai/reset', { method: 'POST' });
        } catch (_) {}
        // Fjern alle messages undtagen welcome
        [...messages.children].forEach((m) => {
            if (!m.classList.contains('ai-welcome')) m.remove();
        });
        statusEl.textContent = 'Samtale nulstillet';
        setTimeout(() => statusEl.textContent = '', 2500);
    });

    form && form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const question = text.value.trim();
        if (!question) return;

        send.disabled = true;
        text.value = '';
        text.style.height = 'auto';

        addMessage('user', question);
        const loading = addMessage('assistant', '', 'loading');

        try {
            const res = await fetch('/ai/chat', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ message: question })
            });

            const data = await res.json();
            loading.remove();

            if (!res.ok || data.error) {
                addMessage('assistant', data.error || 'Noget gik galt.', 'error');
            } else {
                addMessage('assistant', data.reply || '(tomt svar)');
                if (typeof data.remaining === 'number' && data.remaining < 10) {
                    statusEl.textContent = data.remaining + ' tilbage i denne time';
                }
            }
        } catch (err) {
            loading.remove();
            addMessage('assistant', 'Kunne ikke nå serveren.', 'error');
        } finally {
            send.disabled = false;
            text.focus();
        }
    });
})();
