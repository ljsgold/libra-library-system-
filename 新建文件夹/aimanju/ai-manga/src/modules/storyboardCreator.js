const config = require('../../config/config');

class StoryboardCreator {
  constructor() {
    this.apiKey = config.aiModels.script.apiKey;
    this.model = config.aiModels.script.model;
  }

  // 创建分镜
  async create(script, characters, scenes) {
    console.log(`使用 ${this.model} 模型创建分镜`);
    
    const storyboard = [];
    
    // 根据脚本场景生成分镜
    for (let i = 0; i < script.scenes.length; i++) {
      const scene = script.scenes[i];
      
      // 为每个场景生成多个分镜
      for (let j = 0; j < 3; j++) {
        const frame = {
          id: storyboard.length + 1,
          sceneId: scene.id,
          sceneTitle: scene.title,
          description: this.generateFrameDescription(scene, j),
          characters: this.getSceneCharacters(characters, scene),
          cameraAngle: this.generateCameraAngle(j),
          duration: 2 // 每个分镜持续2秒
        };
        
        storyboard.push(frame);
      }
    }
    
    return storyboard;
  }

  // 生成分镜描述
  generateFrameDescription(scene, frameIndex) {
    const descriptions = [
      `开场镜头：${scene.description}`,
      `中景镜头：${scene.description}`,
      `特写镜头：${scene.description}`
    ];
    return descriptions[frameIndex % descriptions.length];
  }

  // 获取场景中的角色
  getSceneCharacters(characters, scene) {
    // 简单起见，返回所有角色
    return characters;
  }

  // 生成相机角度
  generateCameraAngle(frameIndex) {
    const angles = ['全景', '中景', '特写'];
    return angles[frameIndex % angles.length];
  }
}

module.exports = new StoryboardCreator();