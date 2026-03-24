const AIMangaProduction = require('../src/index');

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
  try {
    const result = await mangaProduction.completeProduction(
      projectName,
      plot,
      style,
      characterDescriptions,
      sceneDescriptions
    );
    
    console.log('制作结果:', result);
    console.log('AI漫剧制作完成！');
  } catch (error) {
    console.error('制作过程中出现错误:', error);
  }
}

// 运行示例
main();