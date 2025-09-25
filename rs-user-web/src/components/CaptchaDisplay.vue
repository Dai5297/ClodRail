<template>
  <div class="s-canvas">
    <canvas
      id="s-canvas"
      :width="contentWidth"
      :height="contentHeight"
    ></canvas>
  </div>
</template>

<script setup lang="ts">
import { onMounted, watch, defineProps } from "vue";

const props = defineProps({
  identifyCode: {
    type: String,
    default: "aaaa",
    required: true,
  },
  fontSizeMin: {
    type: Number,
    default: 25,
  },
  fontSizeMax: {
    type: Number,
    default: 35,
  },
  backgroundColorMin: {
    type: Number,
    default: 200,
  },
  backgroundColorMax: {
    type: Number,
    default: 220,
  },
  dotColorMin: {
    type: Number,
    default: 60,
  },
  dotColorMax: {
    type: Number,
    default: 120,
  },
  contentWidth: {
    type: Number,
    default: 90,
  },
  contentHeight: {
    type: Number,
    default: 38,
  },
});

const randomNum = (min, max) => {
  return Math.floor(Math.random() * (max - min) + min);
};

const randomColor = (min, max) => {
  const r = randomNum(min, max);
  const g = randomNum(min, max);
  const b = randomNum(min, max);
  return `rgb(${r},${g},${b})`;
};

const drawText = (ctx, txt, i) => {
  ctx.fillStyle = randomColor(50, 160);
  ctx.font = `${randomNum(props.fontSizeMin, props.fontSizeMax)}px SimHei`;
  const x = (i + 1) * (props.contentWidth / (props.identifyCode.length + 1));
  const y = randomNum(props.fontSizeMax, props.contentHeight - 5);
  const deg = randomNum(-30, 30);

  ctx.translate(x, y);
  ctx.rotate((deg * Math.PI) / 180);
  ctx.fillText(txt, 0, 0);
  ctx.rotate((-deg * Math.PI) / 180);
  ctx.translate(-x, -y);
};

const drawLine = (ctx) => {
  for (let i = 0; i < 4; i++) {
    ctx.strokeStyle = randomColor(100, 200);
    ctx.beginPath();
    ctx.moveTo(
      randomNum(0, props.contentWidth),
      randomNum(0, props.contentHeight)
    );
    ctx.lineTo(
      randomNum(0, props.contentWidth),
      randomNum(0, props.contentHeight)
    );
    ctx.stroke();
  }
};

const drawDot = (ctx) => {
  for (let i = 0; i < 30; i++) {
    ctx.fillStyle = randomColor(0, 255);
    ctx.beginPath();
    ctx.arc(
      randomNum(0, props.contentWidth),
      randomNum(0, props.contentHeight),
      1,
      0,
      2 * Math.PI
    );
    ctx.fill();
  }
};

const drawPic = () => {
  const canvas = document.getElementById("s-canvas");
  const ctx = canvas.getContext("2d");
  ctx.textBaseline = "bottom";

  // 绘制背景
  ctx.fillStyle = "#e6ecfd";
  ctx.fillRect(0, 0, props.contentWidth, props.contentHeight);

  // 绘制文字
  for (let i = 0; i < props.identifyCode.length; i++) {
    drawText(ctx, props.identifyCode[i], i);
  }

  // 绘制干扰线和点
  drawLine(ctx);
  drawDot(ctx);
};

onMounted(() => {
  console.log(props.identifyCode)
  drawPic();
});

watch(() => props.identifyCode, () => {
  drawPic();
});
</script>
