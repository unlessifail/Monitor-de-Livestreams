/**
 * YouTube Webhook Manager - Logic Layer
 * Desenvolvido por Kalil M. Santos - Hexenkult 2026
 */

document.addEventListener('DOMContentLoaded', () => {
    // 1. CONSTANTES E ESTADO GLOBAL
    const API_BASE_URL = 'http://localhost:8080/api/v1/channels';
    
    // Seletores de Configuração de Infra
    const callbackInput = document.getElementById('callback-input');
    const saveConfigBtn = document.getElementById('save-config-btn');
    const finalUrlPreview = document.getElementById('final-url-preview');
    const connectionStatus = document.getElementById('connection-status');

    // Seletores de Operação
    const searchBtn = document.getElementById('search-btn');
    const searchInput = document.getElementById('search-input');
    const searchResultsGrid = document.getElementById('search-results-grid');
    const monitoredListContainer = document.getElementById('monitored-list');

    // 2. GESTÃO DE INFRAESTRUTURA (WEBHOOK ENDPOINT)
    const getStoredCallback = () => localStorage.getItem('ngrok_callback_url');

    const updateUIEndpoint = (url) => {
        if (!url) return;
        const fullEndpoint = `${url}/api/v1/notifications`;
        finalUrlPreview.textContent = fullEndpoint;
        connectionStatus.innerHTML = `<span class="w-2 h-2 bg-green-400 rounded-full animate-pulse"></span> Online`;
        connectionStatus.classList.replace('text-gray-500', 'text-green-400');
    };

    // Inicialização do Estado de Configuração
    const initialUrl = getStoredCallback();
    if (initialUrl) {
        callbackInput.value = initialUrl;
        updateUIEndpoint(initialUrl);
    }

    saveConfigBtn.addEventListener('click', () => {
        const rawUrl = callbackInput.value.trim().replace(/\/$/, ""); // Remove trailing slash
        if (rawUrl.startsWith('http')) {
            localStorage.setItem('ngrok_callback_url', rawUrl);
            updateUIEndpoint(rawUrl);
            alert('Protocolo de comunicação atualizado.');
        } else {
            alert('Input Inválido: Requer protocolo HTTP/HTTPS.');
        }
    });

    // 3. FLUXO DE BUSCA (STEP 1)
    searchBtn.addEventListener('click', async () => {
        const query = searchInput.value.trim();
        if (!query) return;

        searchBtn.disabled = true;
        searchBtn.innerHTML = `<i class="bi bi-arrow-repeat animate-spin"></i> Sincronizando...`;

        try {
            const response = await fetch(`${API_BASE_URL}/search?query=${encodeURIComponent(query)}`, {
                method: 'POST'
            });
            const result = await response.json();

            if (response.ok && result.success) {
                renderSearchResults(result.data);
            }
        } catch (error) {
            console.error("Critical: API Unreachable", error);
            alert("Falha na conexão com o Backend Java.");
        } finally {
            searchBtn.disabled = false;
            searchBtn.innerHTML = `Executar Search <i class="bi bi-chevron-right ml-2"></i>`;
        }
    });

    // 4. RENDERIZAÇÃO DE DISCOVERY (STEP 2)
    function renderSearchResults(channels) {
        searchResultsGrid.innerHTML = channels.map(channel => `
            <div class="glass p-5 rounded-2xl border border-white/5 hover:border-purple-500/50 transition-all flex flex-col gap-4">
                <div class="flex items-center gap-4">
                    <img src="${channel.thumbnailUrl || 'https://via.placeholder.com/60'}" 
                         class="w-14 h-14 rounded-full border-2 border-purple-500 shadow-lg">
                    <div class="overflow-hidden">
                        <p class="font-bold text-white truncate text-sm">${channel.displayName}</p>
                        <p class="text-[10px] text-gray-500 font-mono truncate">${channel.channelId}</p>
                    </div>
                </div>
                <button onclick="confirmHandshake('${channel.channelId}')" 
                        class="w-full bg-purple-600 hover:bg-purple-500 text-white text-[10px] font-black py-3 rounded-xl transition-all uppercase tracking-widest">
                    Confirm Handshake
                </button>
            </div>
        `).join('');
    }

    // 5. SUBSCRIPTION & HANDSHAKE (STEP 3)
    window.confirmHandshake = async (channelId) => {
        const callbackBase = getStoredCallback();
        if (!callbackBase) {
            alert("Erro: Defina o Endpoint de Callback antes de prosseguir.");
            callbackInput.focus();
            return;
        }

        try {
            // Enviamos a URL de callback dinamicamente no header ou param se necessário
            const response = await fetch(`${API_BASE_URL}/confirm/${channelId}?callback=${encodeURIComponent(callbackBase)}`, {
                method: 'POST'
            });
            const result = await response.json();

            if (result.success) {
                searchInput.value = '';
                searchResultsGrid.innerHTML = '';
                fetchMonitoredChannels();
            }
        } catch (error) {
            alert("Handshake Failure: Verifique logs do servidor.");
        }
    };

    // 6. DASHBOARD MONITOR (REACTIVE LIST)
    async function fetchMonitoredChannels() {
        try {
            const response = await fetch(API_BASE_URL); 
            const result = await response.json();

            if (result.success && result.data.length > 0) {
                monitoredListContainer.innerHTML = `
                    <div class="w-full text-left mb-6 border-l-2 border-purple-500 pl-4">
                        <h3 class="text-sm font-black text-white uppercase tracking-widest">Active Radar</h3>
                    </div>
                    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                        ${result.data.map(channel => `
                            <div class="glass p-4 rounded-2xl border border-green-500/10 flex items-center justify-between group">
                                <div class="flex items-center gap-3 overflow-hidden">
                                    <div class="relative">
                                        <img src="${channel.thumbnailUrl}" class="w-10 h-10 rounded-full grayscale group-hover:grayscale-0 transition-all border border-white/10">
                                        <span class="absolute -bottom-1 -right-1 w-3 h-3 bg-green-500 border-2 border-[#0f172a] rounded-full"></span>
                                    </div>
                                    <div class="overflow-hidden">
                                        <p class="text-xs font-bold text-white truncate">${channel.displayName}</p>
                                        <p class="text-[8px] text-gray-500 font-mono uppercase tracking-tighter">Monitoring Active</p>
                                    </div>
                                </div>
                                <button onclick="terminateMonitor('${channel.id}')" class="text-gray-600 hover:text-red-500 transition-colors">
                                    <i class="bi bi-x-circle text-lg"></i>
                                </button>
                            </div>
                        `).join('')}
                    </div>`;
            }
        } catch (e) { console.error("Dashboard Sync Error"); }
    }

    window.terminateMonitor = async (id) => {
        if (!confirm('Deseja encerrar o rastreio deste alvo?')) return;
        await fetch(`${API_BASE_URL}/${id}`, { method: 'DELETE' });
        fetchMonitoredChannels();
    };

    // INIT
    fetchMonitoredChannels();
});