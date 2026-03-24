const fs = require('fs');
const path = require('path');
const config = require('../config/config');

// 导入功能模块
const scriptGenerator = require('./modules/scriptGenerator');
const characterDesigner = require('./modules/characterDesigner');
const sceneDesigner = require('./modules/sceneDesigner');
const storyboardCreator = require('./modules/storyboardCreator');
const animationGenerator = require('./modules/animationGenerator');
const voiceoverGenerator = require('./modules/voiceoverGenerator');
const postProcessor = require('./modules/postProcessor');

class AIMangaProduction {
  constructor() {
    this.projectName = '';
    this.script = '';
    this.characters = [];
    this.scenes = [];
    this.storyboard = [];
    this.animations = [];
    this.voiceovers = [];
  }

  // 初始化项目
  async init(projectName) {
    this.projectName = projectName;
    const projectPath = path.join(config.outputDir, projectName);
    
    // 创建项目目录
    if (!fs.existsSync(projectPath)) {
      fs.mkdirSync(projectPath, { recursive: true });
      console.log(`项目 ${projectName} 初始化成功`);
    }
    
    return projectPath;
  }

  // 生成脚本
  async generateScript(plot, style) {
    console.log('正在生成脚本...');
    this.script = await scriptGenerator.generate(plot, style);
    console.log('脚本生成完成');
    return this.script;
  }

  // 设计角色
  async designCharacters(characterDescriptions) {
    console.log('正在设计角色...');
    this.characters = await characterDesigner.design(characterDescriptions);
    console.log('角色设计完成');
    return this.characters;
  }

  // 设计场景
  async designScenes(sceneDescriptions) {
    console.log('正在设计场景...');
    this.scenes = await sceneDesigner.design(sceneDescriptions);
    console.log('场景设计完成');
    return this.scenes;
  }

  // 创建分镜
  async createStoryboard() {
    console.log('正在创建分镜...');
    this.storyboard = await storyboardCreator.create(this.script, this.characters, this.scenes);
    console.log('分镜创建完成');
    return this.storyboard;
  }

  // 生成动画
  async generateAnimations() {
    console.log('正在生成动画...');
    this.animations = await animationGenerator.generate(this.storyboard);
    console.log('动画生成完成');
    return this.animations;
  }

  // 生成配音
  async generateVoiceovers() {
    console.log('正在生成配音...');
    this.voiceovers = await voiceoverGenerator.generate(this.script);
    console.log('配音生成完成');
    return this.voiceovers;
  }

  // 后期处理
  async postProcess() {
    console.log('正在进行后期处理...');
    const result = await postProcessor.process(this.animations, this.voiceovers);
    console.log('后期处理完成');
    return result;
  }

  // 完整制作流程
  async completeProduction(projectName, plot, style, characterDescriptions, sceneDescriptions) {
    console.log(`开始制作 AI 漫剧: ${projectName}`);
    
    // 1. 初始化项目
    const projectPath = await this.init(projectName);
    
    // 2. 生成脚本
    await this.generateScript(plot, style);
    
    // 3. 设计角色
    await this.designCharacters(characterDescriptions);
    
    // 4. 设计场景
    await this.designScenes(sceneDescriptions);
    
    // 5. 创建分镜
    await this.createStoryboard();
    
    // 6. 生成动画
    await this.generateAnimations();
    
    // 7. 生成配音
    await this.generateVoiceovers();
    
    // 8. 后期处理
    const finalResult = await this.postProcess();
    
    console.log(`AI 漫剧制作完成: ${projectName}`);
    return finalResult;
  }
}

// 导出模块
module.exports = AIMangaProduction;