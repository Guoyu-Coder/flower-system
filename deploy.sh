#!/bin/bash

# =====================================================
# 鲜花系统 Docker 部署脚本
# 位置: deploy.sh
# 说明: 一键启动/停止/重启鲜花系统 (Linux/Mac)
# =====================================================

DOCKER_COMPOSE_FILE="docker-compose.yml"
PROJECT_NAME="flower-system"

function print_menu() {
    clear
    echo ""
    echo "====================================================="
    echo "          ${PROJECT_NAME} Docker 部署脚本"
    echo "====================================================="
    echo ""
    echo "  1. 启动服务 (start)"
    echo "  2. 停止服务 (stop)"
    echo "  3. 重启服务 (restart)"
    echo "  4. 查看日志 (logs)"
    echo "  5. 查看状态 (status)"
    echo "  6. 构建镜像 (build)"
    echo "  7. 清理容器 (clean)"
    echo "  8. 退出 (exit)"
    echo ""
}

function start_service() {
    echo ""
    echo "====================================================="
    echo "            启动 ${PROJECT_NAME} 服务"
    echo "====================================================="
    echo ""
    docker-compose -f ${DOCKER_COMPOSE_FILE} up -d
    echo ""
    echo "服务启动中，请等待约30秒..."
    sleep 30
    echo ""
    echo "服务状态:"
    docker-compose -f ${DOCKER_COMPOSE_FILE} ps
    echo ""
    echo "访问地址: http://localhost"
}

function stop_service() {
    echo ""
    echo "====================================================="
    echo "            停止 ${PROJECT_NAME} 服务"
    echo "====================================================="
    echo ""
    docker-compose -f ${DOCKER_COMPOSE_FILE} down
    echo ""
    echo "服务已停止"
}

function restart_service() {
    stop_service
    start_service
}

function show_logs() {
    echo ""
    echo "====================================================="
    echo "            查看 ${PROJECT_NAME} 日志"
    echo "====================================================="
    echo ""
    read -p "输入服务名称查看日志 (backend/frontend/mysql/redis)，直接回车查看全部: " service
    if [ -z "$service" ]; then
        docker-compose -f ${DOCKER_COMPOSE_FILE} logs -f
    else
        docker-compose -f ${DOCKER_COMPOSE_FILE} logs -f $service
    fi
}

function show_status() {
    echo ""
    echo "====================================================="
    echo "            ${PROJECT_NAME} 服务状态"
    echo "====================================================="
    echo ""
    docker-compose -f ${DOCKER_COMPOSE_FILE} ps
    echo ""
    echo "端口映射:"
    echo "  - 前端: http://localhost:80"
    echo "  - 后端: http://localhost:8088"
    echo "  - MySQL: localhost:3306"
    echo "  - Redis: localhost:6379"
}

function build_images() {
    echo ""
    echo "====================================================="
    echo "            构建 ${PROJECT_NAME} 镜像"
    echo "====================================================="
    echo ""
    docker-compose -f ${DOCKER_COMPOSE_FILE} build --no-cache
    echo ""
    echo "镜像构建完成"
}

function clean_all() {
    echo ""
    echo "====================================================="
    echo "            清理 ${PROJECT_NAME} 容器和镜像"
    echo "====================================================="
    echo ""
    read -p "警告: 此操作将删除所有容器、网络和镜像，确认清理? (y/n): " confirm
    if [ "$confirm" == "y" ] || [ "$confirm" == "Y" ]; then
        docker-compose -f ${DOCKER_COMPOSE_FILE} down --rmi all -v
        echo ""
        echo "清理完成"
    else
        echo "操作已取消"
    fi
}

while true; do
    print_menu
    read -p "请输入操作编号: " choice

    case $choice in
        1) start_service ;;
        2) stop_service ;;
        3) restart_service ;;
        4) show_logs ;;
        5) show_status ;;
        6) build_images ;;
        7) clean_all ;;
        8) echo "退出脚本"; exit 0 ;;
        *) echo "无效的选择，请重新输入" ;;
    esac

    echo ""
    read -p "按 Enter 键继续..."
done
