#!/usr/bin/env python3
"""
花语轩鲜花商城 - 商品图片生成脚本
生成33种鲜花商品的高清封面图及轮播图
保存到 public/images/products/ 目录
"""

import os
import math
from PIL import Image, ImageDraw, ImageFont, ImageFilter
import colorsys

OUTPUT_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'public', 'images', 'products')
os.makedirs(OUTPUT_DIR, exist_ok=True)

# 商品图片定义: (文件名, 标题, 主色, 副色, 花类型, 描述)
PRODUCTS = [
    # 玫瑰系列
    ('red_rose_99.jpg', '红玫瑰花束 "心动"', '#CC0000', '#8B0000', 'rose_red', '99朵厄瓜多尔红玫瑰'),
    ('pink_rose_33.jpg', '粉色玫瑰花束 "初恋"', '#FFB6C1', '#FF69B4', 'rose_pink', '33朵粉玫瑰'),
    ('white_rose_19.jpg', '白玫瑰花束 "纯真"', '#F5F5F5', '#E8E8E8', 'rose_white', '19朵白玫瑰'),
    ('champagne_rose_52.jpg', '香槟玫瑰花束 "优雅"', '#F7E7CE', '#E8CFA0', 'rose_champagne', '52朵香槟玫瑰'),
    ('blue_rose_9.jpg', '蓝色玫瑰花束 "海之恋"', '#4169E1', '#1E90FF', 'rose_blue', '9朵蓝色妖姬'),
    ('rainbow_rose.jpg', '彩虹玫瑰花束 "多彩"', '#FF1493', '#FFD700', 'rose_rainbow', '7色彩虹玫瑰'),
    ('red_rose_11.jpg', '红玫瑰花束 "热恋"', '#DC143C', '#FF4500', 'rose_red2', '11朵红玫瑰热恋'),
    ('pink_snow.jpg', '粉雪山玫瑰花束 "温柔"', '#FFD1DC', '#FFB7C5', 'rose_pink_snow', '20朵粉雪山玫瑰'),

    # 百合系列
    ('white_lily_6.jpg', '白百合花束 "圣洁"', '#FFFAF0', '#FFF0F5', 'lily_white', '6头精品白百合'),
    ('pink_lily.jpg', '粉色百合花束 "温馨"', '#FFB7C5', '#FF91A4', 'lily_pink', '多头粉百合'),
    ('perfume_lily.jpg', '香水百合花束 "馥郁"', '#FFF5EE', '#FFE4E1', 'lily_perfume', '多头香水百合'),
    ('multi_lily.jpg', '多头百合花束 "清香"', '#FDF5E6', '#FAEBD7', 'lily_multi', '多头百合清香'),

    # 康乃馨系列
    ('carnation_mom.jpg', '康乃馨花束 "感恩母亲"', '#FFB6C1', '#FF69B4', 'carnation_pink', '康乃馨感恩花束'),
    ('carnation_basket.jpg', '彩色康乃馨花篮 "祝福"', '#FF7F50', '#FF6347', 'carnation_basket', '多彩康乃馨花篮'),
    ('carnation_red.jpg', '红色康乃馨花束 "敬爱"', '#DC143C', '#B22222', 'carnation_red', '红色康乃馨火热祝福'),

    # 向日葵系列
    ('sunflower_5.jpg', '向日葵花束 "向阳而生"', '#FFD700', '#FFA500', 'sunflower', '5支向日葵阳光花束'),
    ('sunflower_mix.jpg', '向日葵混搭花束 "阳光"', '#FFD700', '#FF8C00', 'sunflower_mix', '向日葵搭配雏菊'),
    ('sunflower_single.jpg', '向日葵单支装 "小确幸"', '#FFD700', '#FFA500', 'sunflower_single', '单支向日葵精致包装'),

    # 郁金香系列
    ('tulip_dutch.jpg', '郁金香花束 "春日浪漫"', '#FF6347', '#FF4500', 'tulip_red', '荷兰进口郁金香'),
    ('tulip_purple.jpg', '紫色郁金香花束 "神秘"', '#9370DB', '#8A2BE2', 'tulip_purple', '紫色郁金香'),
    ('tulip_mix.jpg', '彩色郁金香混搭 "彩虹"', '#FF69B4', '#FFD700', 'tulip_mix', '彩色郁金香缤纷组合'),

    # 混搭花束
    ('mix_garden.jpg', '混搭花束 "缤纷花园"', '#FF69B4', '#9370DB', 'mix_garden', '玫瑰雏菊混搭花园风'),
    ('mix_monet.jpg', '混搭花束 "莫奈花园"', '#DDA0DD', '#87CEEB', 'mix_monet', '法式复古混搭花束'),
    ('mix_small.jpg', '混搭小花束 "清新"', '#98FB98', '#87CEEB', 'mix_small', '小清新日常花束'),

    # 节日礼盒
    ('valentine_box.jpg', '情人节礼盒 "一生一世"', '#FF1493', '#FF0000', 'gift_valentine', '永生花礼盒+巧克力'),
    ('mom_gift_box.jpg', '母亲节礼盒 "感恩的心"', '#FFB6C1', '#FF69B4', 'gift_mom', '康乃馨花束+保温杯'),
    ('christmas_box.jpg', '圣诞节花礼 "圣诞快乐"', '#DC143C', '#228B22', 'gift_christmas', '圣诞红玫瑰礼盒'),
    ('qixi_box.jpg', '七夕礼盒 "鹊桥相会"', '#FF1493', '#4B0082', 'gift_qixi', '七夕限定玫瑰礼盒'),
    ('teacher_day.jpg', '教师节花束 "桃李芬芳"', '#FFD700', '#FF6347', 'gift_teacher', '向日葵+康乃馨教师节'),

    # 绿植盆栽
    ('green_pothos.jpg', '绿萝盆栽', '#228B22', '#32CD32', 'plant_pothos', '大叶绿萝净化空气'),
    ('succulent.jpg', '多肉植物拼盘', '#90EE90', '#FFB6C1', 'plant_succulent', '8种多肉精致组合'),
    ('lucky_bamboo.jpg', '富贵竹', '#006400', '#228B22', 'plant_bamboo', '转运竹富贵吉祥'),
    ('hydrangea.jpg', '绣球花束 "团圆"', '#4169E1', '#6495ED', 'flower_hydrangea', '蓝色绣球花簇拥'),
]

def draw_gradient_bg(draw, size, color_top, color_bottom):
    """绘制渐变背景"""
    width, height = size
    for y in range(height):
        ratio = y / height
        r = int(color_top[0] * (1 - ratio) + color_bottom[0] * ratio)
        g = int(color_top[1] * (1 - ratio) + color_bottom[1] * ratio)
        b = int(color_top[2] * (1 - ratio) + color_bottom[2] * ratio)
        draw.line([(0, y), (width, y)], fill=(r, g, b))

def hex_to_rgb(hex_color):
    """转换十六进制颜色为RGB"""
    hex_color = hex_color.lstrip('#')
    return tuple(int(hex_color[i:i+2], 16) for i in (0, 2, 4))

def lighten_color(rgb, factor=0.3):
    """变亮颜色"""
    r, g, b = rgb
    return (
        min(255, int(r + (255 - r) * factor)),
        min(255, int(g + (255 - g) * factor)),
        min(255, int(b + (255 - b) * factor))
    )

def draw_rose(draw, cx, cy, size, color, rotation=0):
    """画一朵玫瑰"""
    r, g, b = color
    petals_count = 8
    
    # 外花瓣
    for i in range(petals_count):
        angle = math.radians(i * 45 + rotation)
        px = cx + math.cos(angle) * size * 0.5
        py = cy + math.sin(angle) * size * 0.5
        
        # 花瓣弧形
        petal_size = size * 0.35
        for j in range(3):
            s = petal_size * (1 - j * 0.2)
            alpha = 255 - j * 60
            color_petal = (min(255, r + j * 20), max(0, g - j * 10), max(0, b - j * 10), alpha)
            draw.ellipse(
                [px - s, py - s * 0.6, px + s, py + s * 0.6],
                fill=color_petal, outline=None
            )
    
    # 内层花瓣（更紧凑）
    for i in range(6):
        angle = math.radians(i * 60 + 30 + rotation)
        px = cx + math.cos(angle) * size * 0.25
        py = cy + math.sin(angle) * size * 0.25
        petal_size = size * 0.25
        color_inner = (min(255, int(r * 1.2)), max(0, int(g * 0.8)), max(0, int(b * 0.8)))
        draw.ellipse(
            [px - petal_size, py - petal_size * 0.5, px + petal_size, py + petal_size * 0.5],
            fill=color_inner, outline=None
        )
    
    # 花心
    draw.ellipse(
        [cx - size * 0.12, cy - size * 0.12, cx + size * 0.12, cy + size * 0.12],
        fill=(min(255, int(r * 0.8)), max(0, int(g * 0.7)), max(0, int(b * 0.7)))
    )

def draw_lily(draw, cx, cy, size, color, rotation=0):
    """画一朵百合"""
    r, g, b = color
    for i in range(6):
        angle = math.radians(i * 60 + rotation)
        dx = math.cos(angle) * size * 0.3
        dy = math.sin(angle) * size * 0.3
        px = cx + dx
        py = cy + dy
        
        # 花瓣 - 使用更稳定的椭圆绘制
        for j in range(3):
            s = size * (0.4 + j * 0.05)
            w = size * 0.12 * (1 - j * 0.15)
            ex = dx * 0.5
            ey = dy * 0.5
            x0 = px - w + ex
            y0 = py - w + ey
            x1 = px + w + ex
            y1 = py + w + ey
            # 确保 x0 < x1, y0 < y1
            if x0 > x1: x0, x1 = x1, x0
            if y0 > y1: y0, y1 = y1, y0
            c = (min(255, int(r * (1 + j * 0.1))), min(255, int(g * (1 + j * 0.1))), min(255, int(b * (1 + j * 0.1))))
            draw.ellipse([x0, y0, x1, y1], fill=c, outline=None)
    
    # 花蕊
    for i in range(5):
        angle = math.radians(i * 72 + rotation)
        rx = cx + math.cos(angle) * size * 0.15
        ry = cy + math.sin(angle) * size * 0.15
        draw.ellipse([rx - 3, ry - 3, rx + 3, ry + 3], fill=(255, 200, 50))

def draw_carnation(draw, cx, cy, size, color, rotation=0):
    """画一朵康乃馨（锯齿状花瓣）"""
    r, g, b = color
    for i in range(12):
        angle = math.radians(i * 30 + rotation)
        px = cx + math.cos(angle) * size * 0.1
        py = cy + math.sin(angle) * size * 0.1
        
        petal_size = size * 0.3
        for j in range(4):
            s = petal_size * (1 - j * 0.15)
            offset_angle = angle + math.radians(j * 15)
            ox = math.cos(offset_angle) * s * 0.4
            oy = math.sin(offset_angle) * s * 0.4
            c = (
                min(255, max(0, int(r + j * 15))),
                min(255, max(0, int(g + j * 10))),
                min(255, max(0, int(b + j * 15))),
                200 - j * 30
            )
            draw.ellipse([px + ox - s*0.3, py + oy - s*0.3, px + ox + s*0.3, py + oy + s*0.3], fill=c, outline=None)

def draw_sunflower(draw, cx, cy, size, color, rotation=0):
    """画一朵向日葵"""
    r, g, b = color
    
    # 花瓣层
    for layer in range(2):
        num_petals = 16 - layer * 4
        for i in range(num_petals):
            angle = math.radians(i * (360 / num_petals) + rotation + layer * 10)
            px = cx + math.cos(angle) * size * (0.2 + layer * 0.1)
            py = cy + math.sin(angle) * size * (0.2 + layer * 0.1)
            
            petal_w = size * 0.15
            petal_h = size * 0.35 + layer * 0.1
            c = (
                min(255, int(r * (1 - layer * 0.1))),
                min(255, int(g * (1 - layer * 0.05))),
                max(0, int(b * (1 - layer * 0.1)))
            )
            draw.ellipse([px - petal_w, py - petal_h, px + petal_w, py + petal_h], fill=c, outline=None)
    
    # 花盘（棕色中心）
    for j in range(3):
        s = size * 0.2 * (1 - j * 0.2)
        c = (80 - j * 20, 40 - j * 10, 10)
        draw.ellipse([cx - s, cy - s, cx + s, cy + s], fill=c)
    
    # 种子点
    for i in range(12):
        angle = math.radians(i * 30)
        sx = cx + math.cos(angle) * size * 0.12
        sy = cy + math.sin(angle) * size * 0.12
        draw.ellipse([sx - 2, sy - 2, sx + 2, sy + 2], fill=(60, 30, 5))

def draw_tulip(draw, cx, cy, size, color, rotation=0):
    """画一朵郁金香"""
    r, g, b = color
    
    # 花瓣杯状
    for i in range(3):
        angle = math.radians(i * 120 - 60 + rotation)
        px = cx + math.cos(angle) * size * 0.2
        py = cy + math.sin(angle) * size * 0.2
        
        # 椭圆花瓣
        c = (min(255, int(r * 1.1)), min(255, int(g * 1.1)), min(255, int(b * 1.1)))
        draw.ellipse(
            [px - size*0.2, py - size*0.45, px + size*0.2, py + size*0.05],
            fill=c, outline=None
        )
    
    # 内层花瓣
    for i in range(3):
        angle = math.radians(i * 120 + rotation)
        px = cx + math.cos(angle) * size * 0.1
        py = cy + math.sin(angle) * size * 0.1
        c = (min(255, int(r * 1.3)), min(255, int(g * 1.2)), min(255, int(b * 1.2)))
        draw.ellipse(
            [px - size*0.12, py - size*0.3, px + size*0.12, py + size*0.1],
            fill=c, outline=None
        )

def draw_flower_bouquet(draw, size, flowers, flower_func, base_color, stem_color=(34, 139, 34)):
    """绘制花束（多朵花组合）"""
    width, height = size
    rgb = hex_to_rgb(base_color)
    num_flowers = len(flowers)
    
    # 叶子背景
    for i in range(8):
        angle = math.radians(i * 45 + 10)
        lx = width // 2 + math.cos(angle) * width * 0.3
        ly = height * 0.6 + math.sin(angle) * height * 0.2
        draw.ellipse([lx - 25, ly - 10, lx + 25, ly + 10], fill=(46, 139, 87, 120), outline=None)
    
    # 绘制花茎
    for i in range(num_flowers):
        x, y = flowers[i]
        draw.line([(x, y + 30), (x, height * 0.85)], fill=stem_color, width=4)
    
    # 叶子
    for i in range(num_flowers):
        x, y = flowers[i]
        leaf_y = y + 60 + i * 15
        draw.ellipse([x - 18, leaf_y - 6, x + 18, leaf_y + 6], fill=(50, 160, 50))
        draw.ellipse([x - 15, leaf_y + 10 - 6, x + 15, leaf_y + 10 + 6], fill=(60, 170, 60))
    
    # 绘制花朵（从后到前）
    for i in range(num_flowers - 1, -1, -1):
        x, y = flowers[i]
        flower_size = 40 + i * 3
        rotation = i * 30 + 15
        c = (
            min(255, rgb[0] + i * 10),
            min(255, rgb[1] + i * 5),
            min(255, rgb[2] + i * 8)
        )
        flower_func(draw, x, y, flower_size, c, rotation)

def draw_gift_box(draw, size, color1, color2, icon_text=""):
    """绘制礼盒"""
    width, height = size
    cx, cy = width // 2, height // 2
    
    # 盒子主体
    box_w, box_h = width * 0.55, height * 0.35
    # 盒身
    draw.rounded_rectangle(
        [cx - box_w//2, cy - box_h//4, cx + box_w//2, cy + box_h*0.6],
        radius=8, fill=color1
    )
    # 盒盖
    draw.rounded_rectangle(
        [cx - box_w//2 - 5, cy - box_h*0.45, cx + box_w//2 + 5, cy - box_h//4 + 5],
        radius=6, fill=color2
    )
    # 丝带（垂直）
    draw.rectangle([cx - 6, cy - box_h*0.4, cx + 6, cy + box_h*0.6], fill=(255, 215, 0))
    # 丝带（水平）
    draw.rectangle([cx - box_w//2, cy - 6, cx + box_w//2, cy + 6], fill=(255, 215, 0))
    # 蝴蝶结
    draw.ellipse([cx - 20, cy - box_h*0.45 - 10, cx - 5, cy - box_h*0.45 + 10], fill=(255, 215, 0))
    draw.ellipse([cx + 5, cy - box_h*0.45 - 10, cx + 20, cy - box_h*0.45 + 10], fill=(255, 215, 0))
    draw.ellipse([cx - 5, cy - box_h*0.5, cx + 5, cy - box_h*0.35], fill=(255, 215, 0))

def draw_potted_plant(draw, size, color1, color2, plant_type="pothos"):
    """绘制盆栽"""
    width, height = size
    cx, cy = width // 2, height // 2
    
    # 花盆
    pot_bottom = cy + height * 0.3
    pot_top_w = width * 0.35
    pot_bottom_w = width * 0.3
    pot_h = height * 0.35
    
    # 花盆梯形
    draw.polygon([
        (cx - pot_top_w//2, pot_bottom - pot_h),
        (cx + pot_top_w//2, pot_bottom - pot_h),
        (cx + pot_bottom_w//2, pot_bottom),
        (cx - pot_bottom_w//2, pot_bottom)
    ], fill=(210, 105, 30))
    
    # 盆沿
    draw.rounded_rectangle(
        [cx - pot_top_w//2 - 5, pot_bottom - pot_h - 8, cx + pot_top_w//2 + 5, pot_bottom - pot_h + 5],
        radius=4, fill=(222, 120, 40)
    )
    
    # 土壤
    draw.ellipse(
        [cx - pot_top_w//2 + 5, pot_bottom - pot_h - 5, cx + pot_top_w//2 - 5, pot_bottom - pot_h + 10],
        fill=(101, 67, 33)
    )
    
    # 叶子
    if plant_type == "pothos":
        for i in range(5):
            angle = math.radians(i * 72 - 90)
            lx = cx + math.cos(angle) * width * 0.15
            ly = pot_bottom - pot_h - 20 + math.sin(angle) * 20
            leaf_size = 20 + i * 5
            draw.ellipse([lx - leaf_size, ly - leaf_size*0.6, lx + leaf_size, ly + leaf_size*0.6], fill=color1)
            # 叶脉
            draw.line([(lx - leaf_size*0.5, ly), (lx + leaf_size*0.5, ly)], fill=(0, 80, 0), width=1)
    elif plant_type == "succulent":
        # 多肉莲座状
        for layer in range(4):
            num = 8 - layer * 2
            s = 25 - layer * 5
            for i in range(num):
                angle = math.radians(i * (360 / num) + layer * 20)
                px = cx + math.cos(angle) * (8 + layer * 10)
                py = pot_bottom - pot_h - 10 + math.sin(angle) * 5
                c = (
                    max(0, color1[0] - layer * 15),
                    min(255, color1[1] + layer * 10),
                    max(0, color1[2] - layer * 5)
                )
                draw.ellipse([px - s, py - s*0.5, px + s, py + s*0.5], fill=c)
    elif plant_type == "bamboo":
        # 竹子
        for i in range(3):
            bx = cx + (i - 1) * 25
            # 竹竿
            for j in range(4):
                seg_y = pot_bottom - pot_h + 10 - j * 40
                draw.rectangle([bx - 6, seg_y - 15, bx + 6, seg_y + 15], fill=(0, 128, 0))
                # 竹节
                draw.rectangle([bx - 7, seg_y - 18, bx + 7, seg_y - 12], fill=(0, 100, 0))
            # 竹叶
            for j in range(3):
                angle = math.radians(j * 30 + i * 20 - 60)
                lx = bx + math.cos(angle) * 30
                ly = pot_bottom - pot_h - 50 + j * 15
                draw.ellipse([lx - 20, ly - 4, lx + 5, ly + 4], fill=(34, 139, 34))

# ====== 主生成函数 ======

def generate_flower_bouquet(filename, title, color_hex, flower_type):
    """生成花朵类图片"""
    img = Image.new('RGBA', (800, 800), (255, 255, 255, 255))
    draw = ImageDraw.Draw(img)
    
    rgb = hex_to_rgb(color_hex)
    light_rgb = lighten_color(rgb, 0.15)
    
    # 渐变背景
    bg_img = Image.new('RGBA', (800, 800), (255, 255, 255, 0))
    bg_draw = ImageDraw.Draw(bg_img)
    
    # 柔和圆形渐变背景
    for r in range(400, 0, -1):
        alpha = max(0, min(200, int(200 * (1 - r / 400))))
        c = (
            min(255, light_rgb[0] + 50),
            min(255, light_rgb[1] + 30),
            min(255, light_rgb[2] + 40)
        )
        bg_draw.ellipse([400 - r, 350 - r, 400 + r, 350 + r], fill=(*c, alpha // 3))
    
    img.paste(bg_img, (0, 0), bg_img)
    
    # 根据不同类型绘制
    w, h = 800, 800
    flowers = [
        (w // 2 - 60, h * 0.32),
        (w // 2 + 60, h * 0.28),
        (w // 2, h * 0.25),
        (w // 2 - 30, h * 0.38),
        (w // 2 + 30, h * 0.35),
    ]
    
    if 'rose' in flower_type:
        flower_func = draw_rose
    elif 'lily' in flower_type:
        flower_func = draw_lily
    elif 'carnation' in flower_type:
        flower_func = draw_carnation
    elif 'sunflower' in flower_type:
        flower_func = draw_sunflower
    elif 'tulip' in flower_type:
        flower_func = draw_tulip
    elif 'hydrangea' in flower_type:
        # 绣球 - 小簇花
        def draw_hydrangea(dr, cx, cy, sz, clr, rot=0):
            for i in range(16):
                a = math.radians(i * 22.5 + rot)
                px = cx + math.cos(a) * sz * 0.25
                py = cy + math.sin(a) * sz * 0.25
                c = (min(255, clr[0] + i * 3), min(255, clr[1] + i * 2), min(255, clr[2] + i * 5))
                dr.ellipse([px - 8, py - 8, px + 8, py + 8], fill=c)
            dr.ellipse([cx - 10, cy - 10, cx + 10, cy + 10], fill=clr)
        flower_func = draw_hydrangea
    elif 'gift' in flower_type:
        color1 = rgb
        color2 = light_rgb
        draw_gift_box(draw, (800, 800), color1, color2)
        # 添加一些装饰花
        for i in range(3):
            angle = math.radians(i * 120 + 30)
            fx = 400 + math.cos(angle) * 160
            fy = 300 + math.sin(angle) * 60
            draw_rose(draw, fx, fy, 35, (200, 0, 0), i * 30)
        save_image(img, filename)
        return
    elif 'plant' in flower_type:
        plant_type = flower_type.replace('plant_', '')
        draw_potted_plant(draw, (800, 800), rgb, light_rgb, plant_type)
        save_image(img, filename)
        return
    elif 'mix' in flower_type or 'flower' in flower_type:
        # 混合花束 - 多种花
        combo = [(draw_rose, (220, 50, 80)), (draw_lily, (180, 100, 180)), (draw_sunflower, (255, 180, 50)), (draw_carnation, (255, 120, 150))]
        for i, (func, c) in enumerate(combo):
            fx = 300 + i * 60
            fy = 280 + (i % 2) * 40
            func(draw, fx, fy, 35 + i * 3, c, i * 45)
        # 额外一些点缀花
        for i in range(4):
            angle = math.radians(i * 90 + 20)
            fx = 400 + math.cos(angle) * 130
            fy = 320 + math.sin(angle) * 70
            draw_rose(draw, fx, fy, 25, (255, 100, 150), i * 20)
        save_image(img, filename)
        return
    else:
        flower_func = draw_rose
    
    draw_flower_bouquet(draw, (800, 800), flowers, flower_func, color_hex)
    
    # 添加一些装饰性元素 - 满天星点缀
    for i in range(20):
        x = 100 + (i * 60) % 600
        y = 150 + (i * 47) % 500
        draw.ellipse([x - 2, y - 2, x + 2, y + 2], fill=(255, 255, 255, 180))
    
    # 添加一些散射的小光点
    for i in range(15):
        x = 80 + (i * 73) % 640
        y = 100 + (i * 51) % 550
        draw.ellipse([x - 1, y - 1, x + 1, y + 1], fill=(255, 255, 200, 100))
    
    save_image(img, filename)


def save_image(img, filename):
    """保存图片"""
    # 转换为RGB
    if img.mode == 'RGBA':
        bg = Image.new('RGB', img.size, (255, 255, 255))
        bg.paste(img, mask=img.split()[3])
        img = bg
    
    filepath = os.path.join(OUTPUT_DIR, filename)
    # 添加柔化效果
    img = img.filter(ImageFilter.SMOOTH)
    img.save(filepath, 'JPEG', quality=92)
    print(f"  ✓ 已生成: {filename} ({filepath})")


def generate_carousel_image(filename, title, color_hex, flower_type, variant=1):
    """生成轮播图（略有变化的不同角度）"""
    img = Image.new('RGBA', (800, 800), (255, 255, 255, 255))
    draw = ImageDraw.Draw(img)
    
    rgb = hex_to_rgb(color_hex)
    light_rgb = lighten_color(rgb, 0.15)
    
    # 不同变体使用不同的背景色调
    offset = variant * 20
    bg_img = Image.new('RGBA', (800, 800), (255, 255, 255, 0))
    bg_draw = ImageDraw.Draw(bg_img)
    
    for r in range(380, 0, -1):
        alpha = max(0, min(180, int(180 * (1 - r / 380))))
        c = (
            min(255, light_rgb[0] + 30 + offset),
            min(255, light_rgb[1] + 20),
            min(255, light_rgb[2] + 30)
        )
        bg_draw.ellipse([400 - r, 350 - r + variant * 10, 400 + r, 350 + r + variant * 10], fill=(*c, alpha // 3))
    
    img.paste(bg_img, (0, 0), bg_img)
    
    # 花朵 - 不同布局
    w, h = 800, 800
    if variant == 1:
        flowers = [(w // 2, h * 0.32), (w // 2 - 70, h * 0.28), (w // 2 + 70, h * 0.38)]
    elif variant == 2:
        flowers = [(w // 2 - 50, h * 0.30), (w // 2 + 50, h * 0.32), (w // 2, h * 0.38), (w // 2 - 40, h * 0.25)]
    else:
        flowers = [(w // 2, h * 0.28), (w // 2 - 80, h * 0.35), (w // 2 + 80, h * 0.30)]
    
    if 'rose' in flower_type:
        flower_func = draw_rose
    elif 'lily' in flower_type:
        flower_func = draw_lily
    elif 'carnation' in flower_type:
        flower_func = draw_carnation
    elif 'sunflower' in flower_type:
        flower_func = draw_sunflower
    elif 'tulip' in flower_type:
        flower_func = draw_tulip
    elif 'gift' in flower_type:
        color1 = rgb
        color2 = light_rgb
        draw_gift_box(draw, (800, 800), color1, color2)
        for i in range(3):
            angle = math.radians(i * 120 + 30 + variant * 15)
            fx = 400 + math.cos(angle) * 160
            fy = 300 + math.sin(angle) * 60
            draw_rose(draw, fx, fy, 30 + variant * 5, (200, 0, 0), i * 30 + variant * 20)
        save_image(img, filename)
        return
    elif 'plant' in flower_type:
        pt = flower_type.replace('plant_', '')
        draw_potted_plant(draw, (800, 800), rgb, light_rgb, pt)
        save_image(img, filename)
        return
    elif 'mix' in flower_type or 'flower' in flower_type:
        combo = [(draw_rose, (220, 50, 80)), (draw_lily, (180, 100, 180)), (draw_sunflower, (255, 180, 50)), (draw_carnation, (255, 120, 150))]
        for i, (func, c) in enumerate(combo):
            fx = 300 + i * 60 + variant * 10
            fy = 280 + (i % 2) * 40
            func(draw, fx, fy, 35 + i * 3, c, i * 45 + variant * 30)
        save_image(img, filename)
        return
    else:
        flower_func = draw_rose
    
    rot_offset = variant * 20
    draw_flower_bouquet(draw, (800, 800), flowers, flower_func, color_hex)
    
    # 装饰
    for i in range(15 + variant * 5):
        x = 80 + (i * 67) % 640
        y = 120 + (i * 53) % 560
        draw.ellipse([x - 2, y - 2, x + 2, y + 2], fill=(255, 255, 255, 150))
    
    save_image(img, filename)


# ====== 主程序 ======
if __name__ == '__main__':
    print("=" * 50)
    print("花语轩鲜花商城 - 商品图片生成器")
    print("=" * 50)
    print(f"\n输出目录: {OUTPUT_DIR}")
    print(f"共 {len(PRODUCTS)} 个商品\n")
    
    for filename, title, color_hex, _, flower_type, _ in PRODUCTS:
        print(f"生成: {title}")
        
        # 生成封面图
        generate_flower_bouquet(filename, title, color_hex, flower_type)
        
        # 生成轮播图（_1.jpg, _2.jpg）
        base_name = filename.replace('.jpg', '')
        for v in [1, 2]:
            carousel_filename = f"{base_name}_{v}.jpg"
            generate_carousel_image(carousel_filename, title, color_hex, flower_type, v)
    
    print(f"\n{'=' * 50}")
    print(f"✅ 全部生成完成！共生成 {len(PRODUCTS) * 3} 张图片")
    print(f"   - 封面图: {len(PRODUCTS)} 张")
    print(f"   - 轮播图: {len(PRODUCTS) * 2} 张")
    print(f"{'=' * 50}")