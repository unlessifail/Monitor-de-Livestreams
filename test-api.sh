#!/bin/bash

# Script de testes para o YouTube Live Monitor
# Certifique-se de que a aplicação está rodando em localhost:8080

echo "===================================="
echo "YouTube Live Monitor - Script de Testes"
echo "===================================="
echo ""

BASE_URL="http://localhost:8080/api/v1"

# Cores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 1. Buscar canais
echo -e "${BLUE}[1/6] Buscando canais com query 'gaules'...${NC}"
echo ""
curl -X GET "${BASE_URL}/channels/search?query=gaules" | jq '.'
echo ""
echo ""
sleep 2

# 2. Buscar por link
echo -e "${BLUE}[2/6] Buscando canal por link...${NC}"
echo ""
curl -X GET "${BASE_URL}/channels/search?query=youtube.com/@casimito" | jq '.'
echo ""
echo ""
sleep 2

# 3. Confirmar e adicionar canal (substitua pelo ID real obtido na busca)
echo -e "${YELLOW}Para confirmar um canal, use o channelId obtido na busca anterior.${NC}"
echo -e "${YELLOW}Exemplo:${NC}"
echo "curl -X POST '${BASE_URL}/channels/confirm/UCd1y-FSzUvDxx7UP_j72mXQ'"
echo ""
echo -e "${BLUE}[3/6] Pressione ENTER para continuar com os outros testes...${NC}"
read -r
echo ""

# 4. Listar todos os canais
echo -e "${BLUE}[4/6] Listando todos os canais monitorados...${NC}"
echo ""
curl -X GET "${BASE_URL}/channels" | jq '.'
echo ""
echo ""
sleep 2

# 5. Listar canais ativos
echo -e "${BLUE}[5/6] Listando apenas canais ativos...${NC}"
echo ""
curl -X GET "${BASE_URL}/channels/active" | jq '.'
echo ""
echo ""
sleep 2

# 6. Health check do webhook
echo -e "${BLUE}[6/6] Testando health check do webhook...${NC}"
echo ""
curl -X GET "${BASE_URL}/notifications/health"
echo ""
echo ""

echo -e "${GREEN}===================================="
echo "Testes concluídos!"
echo -e "====================================${NC}"
echo ""
echo "Comandos adicionais úteis:"
echo ""
echo "# Buscar detalhes de um canal específico:"
echo "curl -X GET '${BASE_URL}/channels/{channelId}'"
echo ""
echo "# Remover um canal:"
echo "curl -X DELETE '${BASE_URL}/channels/{channelId}'"
echo ""
echo "# Simular callback de verificação (teste local):"
echo "curl -X GET '${BASE_URL}/notifications?hub.mode=subscribe&hub.topic=test&hub.challenge=test123&hub.lease_seconds=432000'"
