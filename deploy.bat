@echo off
setlocal enabledelayedexpansion

REM =====================================================
REM 鲜花系统 Docker 部署脚本
REM 位置: deploy.bat
REM 说明: 一键启动/停止/重启鲜花系统
REM =====================================================

set "DOCKER_COMPOSE_FILE=docker-compose.yml"
set "PROJECT_NAME=flower-system"

goto :menu

:menu
cls
echo.
echo =====================================================
echo          %PROJECT_NAME% Docker 部署脚本
echo =====================================================
echo.
echo  1. 启动服务 (start)
echo  2. 停止服务 (stop)
echo  3. 重启服务 (restart)
echo  4. 查看日志 (logs)
echo  5. 查看状态 (status)
echo  6. 构建镜像 (build)
echo  7. 清理容器 (clean)
echo  8. 退出 (exit)
echo.
set /p "choice=请输入操作编号: "

if "%choice%"=="1" goto :start
if "%choice%"=="2" goto :stop
if "%choice%"=="3" goto :restart
if "%choice%"=="4" goto :logs
if "%choice%"=="5" goto :status
if "%choice%"=="6" goto :build
if "%choice%"=="7" goto :clean
if "%choice%"=="8" goto :exit

echo 无效的选择，请重新输入
pause
goto :menu

:start
echo.
echo =====================================================
echo            启动 %PROJECT_NAME% 服务
echo =====================================================
echo.
docker-compose -f %DOCKER_COMPOSE_FILE% up -d
echo.
echo 服务启动中，请等待约30秒...
timeout /t 30 /nobreak >nul
echo.
echo 服务状态:
docker-compose -f %DOCKER_COMPOSE_FILE% ps
echo.
echo 访问地址: http://localhost
pause
goto :menu

:stop
echo.
echo =====================================================
echo            停止 %PROJECT_NAME% 服务
echo =====================================================
echo.
docker-compose -f %DOCKER_COMPOSE_FILE% down
echo.
echo 服务已停止
pause
goto :menu

:restart
echo.
echo =====================================================
echo            重启 %PROJECT_NAME% 服务
echo =====================================================
echo.
call :stop
call :start
pause
goto :menu

:logs
echo.
echo =====================================================
echo            查看 %PROJECT_NAME% 日志
echo =====================================================
echo.
echo 输入服务名称查看日志 (backend/frontend/mysql/redis)，直接回车查看全部
set /p "service=服务名称: "
if "%service%"=="" (
    docker-compose -f %DOCKER_COMPOSE_FILE% logs -f
) else (
    docker-compose -f %DOCKER_COMPOSE_FILE% logs -f %service%
)
goto :menu

:status
echo.
echo =====================================================
echo            %PROJECT_NAME% 服务状态
echo =====================================================
echo.
docker-compose -f %DOCKER_COMPOSE_FILE% ps
echo.
echo 端口映射:
echo   - 前端: http://localhost:80
echo   - 后端: http://localhost:8088
echo   - MySQL: localhost:3306
echo   - Redis: localhost:6379
pause
goto :menu

:build
echo.
echo =====================================================
echo            构建 %PROJECT_NAME% 镜像
echo =====================================================
echo.
docker-compose -f %DOCKER_COMPOSE_FILE% build --no-cache
echo.
echo 镜像构建完成
pause
goto :menu

:clean
echo.
echo =====================================================
echo            清理 %PROJECT_NAME% 容器和镜像
echo =====================================================
echo.
echo 警告: 此操作将删除所有容器、网络和镜像
set /p "confirm=确认清理? (y/n): "
if /i "%confirm%"=="y" (
    docker-compose -f %DOCKER_COMPOSE_FILE% down --rmi all -v
    echo.
    echo 清理完成
) else (
    echo 操作已取消
)
pause
goto :menu

:exit
echo.
echo =====================================================
echo                    退出脚本
echo =====================================================
echo.
endlocal
exit /b
