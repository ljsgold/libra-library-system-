# AI漫剧制作系统

## 项目介绍

AI漫剧制作系统是一个基于AI技术的漫剧自动生成工具，能够完成从脚本创作、角色设计、场景设计、分镜创建、动画生成、配音到后期处理的完整流程。

## 功能特性

- **脚本生成**：基于AI模型自动生成漫剧脚本
- **角色设计**：根据描述生成角色外观和性格
- **场景设计**：创建符合剧情的场景环境
- **分镜创建**：自动生成分镜脚本
- **动画生成**：将分镜转换为动画
- **配音生成**：为角色对话生成配音
- **后期处理**：合成最终视频

## 项目结构

```
ai-manga/
├── src/             # 源代码
│   ├── index.js     # 主入口文件
│   └── modules/     # 功能模块
│       ├── scriptGenerator.js      # 脚本生成器
│       ├── characterDesigner.js    # 角色设计器
│       ├── sceneDesigner.js        # 场景设计器
│       ├── storyboardCreator.js    # 分镜创建器
│       ├── animationGenerator.js   # 动画生成器
│       ├── voiceoverGenerator.js   # 配音生成器
│       └── postProcessor.js        # 后期处理器
├── config/          # 配置文件
│   └── config.js    # 系统配置
├── scripts/         # 脚本文件
│   └── example.js   # 示例脚本
├── assets/          # 资源目录
├── output/          # 输出目录
├── package.json     # 项目配置
└── README.md        # 项目说明
```

## 安装与使用

### 安装依赖

```bash
npm install
```

### 配置API密钥

在 `config/config.js` 文件中配置AI模型的API密钥：

```javascript
aiModels: {
  script: {
    apiKey: 'YOUR_API_KEY',  // 替换为实际的API密钥
    model: 'gpt-4'
  },
  image: {
    apiKey: 'YOUR_API_KEY',  // 替换为实际的API密钥
    model: 'dall-e-3'
  },
  voice: {
    apiKey: 'YOUR_API_KEY',  // 替换为实际的API密钥
    model: 'elevenlabs'
  }
}
```

### 运行示例

```bash
node scripts/example.js
```

## 使用方法

### 基本用法

```javascript
const AIMangaProduction = require('./src/index');

async function main() {
  // 创建AI漫剧制作实例
  const mangaProduction = new AIMangaProduction();
  
  // 项目配置
  const projectName = '示例漫剧';
  const plot = '一个年轻人发现自己拥有超能力，开始保护城市的故事';
  const style = 'anime';
  
  // 角色描述
  const characterDescriptions = [
    {
      name: '小明',
      description: '普通高中生，意外获得超能力'
    },
    {
      name: '小红',
      description: '小明的同学，聪明勇敢'
    },
    {
      name: '反派',
      description: '企图破坏城市的坏人'
    }
  ];
  
  // 场景描述
  const sceneDescriptions = [
    {
      name: '学校',
      description: '阳光明媚的校园'
    },
    {
      name: '城市街道',
      description: '繁华的城市街道'
    },
    {
      name: ' rooftop',
      description: '高楼屋顶，夜景'
    },
    {
      name: '实验室',
      description: '神秘的实验室'
    },
    {
      name: '战场',
      description: '激烈的战斗场景'
    }
  ];
  
  // 执行完整制作流程
  const result = await mangaProduction.completeProduction(
    projectName,
    plot,
    style,
    characterDescriptions,
    sceneDescriptions
  );
  
  console.log('AI漫剧制作完成！');
  console.log('最终视频URL:', result.finalVideoUrl);
}

main();
```

### 单独使用模块

```javascript
// 单独使用脚本生成器
const scriptGenerator = require('./src/modules/scriptGenerator');
const script = await scriptGenerator.generate('一个关于友谊的故事', 'manga');

// 单独使用角色设计器
const characterDesigner = require('./src/modules/characterDesigner');
const characters = await characterDesigner.design([
  { name: '主角', description: '勇敢的少年' }
]);

// 单独使用场景设计器
const sceneDesigner = require('./src/modules/sceneDesigner');
const scenes = await sceneDesigner.design([
  { name: '森林', description: '神秘的森林' }
]);
```

## 配置选项

在 `config/config.js` 文件中可以配置以下选项：

- **outputDir**：输出目录
- **assetsDir**：资源目录
- **aiModels**：AI模型配置
- **generation**：生成配置
- **styles**：风格配置

## 注意事项

1. 本项目使用了模拟的API调用，实际使用时需要替换为真实的AI模型API
2. 生成的图片和视频URL为模拟地址，实际使用时需要配置真实的存储服务
3. 为了获得最佳效果，建议提供详细的角色和场景描述
4. 生成过程可能需要较长时间，具体取决于AI模型的响应速度

## 许可证

MIT License

## 联系方式

如有问题或建议，请联系我们：
- Email: contact@aimanga.com
- GitHub: https://github.com/aimanga-production
