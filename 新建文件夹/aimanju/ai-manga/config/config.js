const path = require('path');

module.exports = {
  // 输出目录
  outputDir: path.join(__dirname, '..', 'output'),
  
  // 资源目录
  assetsDir: path.join(__dirname, '..', 'assets'),
  
  // AI 模型配置
  aiModels: {
    script: {
      apiKey: 'YOUR_API_KEY',
      model: 'gpt-4'
    },
    image: {
      apiKey: 'YOUR_API_KEY',
      model: 'dall-e-3'
    },
    voice: {
      apiKey: 'YOUR_API_KEY',
      model: 'elevenlabs'
    }
  },
  
  // 生成配置
  generation: {
    scriptLength: 1000, // 脚本长度
    characterCount: 3, // 角色数量
    sceneCount: 5, // 场景数量
    animationDuration: 10, // 动画时长（秒）
    voiceoverQuality: 'high' // 配音质量
  },
  
  // 风格配置
  styles: {
    manga: {
      name: ' manga',
      description: '日式漫画风格'
    },
    anime: {
      name: 'anime',
      description: '日本动画风格'
    },
    western: {
      name: 'western',
      description: '西方漫画风格'
    },
    chibi: {
      name: 'chibi',
      description: 'Q版风格'
    }
  }
};