<template>
  <div class="avatar-upload">
    <el-upload
      class="avatar-uploader"
      :show-file-list="false"
      :before-upload="beforeUpload"
      :http-request="customUpload"
    >
      <div class="upload-content">
        <img v-if="modelValue" :src="modelValue" class="avatar" />
        <div v-else class="avatar-placeholder">
          <el-icon class="avatar-uploader-icon"><Plus /></el-icon>
          <span>点击上传头像</span>
        </div>
        <div class="hover-mask">
          <el-icon><Camera /></el-icon>
          <span>更换头像</span>
        </div>
      </div>
    </el-upload>
    <div class="upload-tip">支持 jpg、png、gif 格式，大小不超过 2MB</div>
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { Plus, Camera } from '@element-plus/icons-vue'
import { uploadAvatar } from '@/api/system/user'

const props = defineProps<{
  modelValue?: string | null
}>()

const emit = defineEmits(['update:modelValue', 'success'])

// 上传前校验
const beforeUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('上传头像图片只能是图片格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 自定义上传
const customUpload = async (options: { file: File }) => {
  try {
    const res = await uploadAvatar(options.file)
    emit('update:modelValue', res.data.url)
    emit('success')
    ElMessage.success('头像上传成功')
  } catch (error: any) {
    ElMessage.error(error.message || '头像上传失败')
  }
}
</script>

<style lang="scss" scoped>
.avatar-upload {
  text-align: center;

  .avatar-uploader {
    border: 2px dashed var(--el-border-color);
    border-radius: 8px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
    width: 120px;
    height: 120px;
    margin: 0 auto;

    &:hover {
      border-color: var(--el-color-primary);
      
      .hover-mask {
        opacity: 1;
      }
    }

    .upload-content {
      width: 100%;
      height: 100%;
      position: relative;
    }

    .avatar-placeholder {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      height: 100%;
      color: var(--el-text-color-secondary);

      .avatar-uploader-icon {
        font-size: 28px;
        margin-bottom: 8px;
      }

      span {
        font-size: 14px;
      }
    }

    .hover-mask {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: rgba(0, 0, 0, 0.6);
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      color: #fff;
      opacity: 0;
      transition: opacity 0.3s;

      .el-icon {
        font-size: 24px;
        margin-bottom: 8px;
      }

      span {
        font-size: 14px;
      }
    }
  }

  .avatar {
    width: 100%;
    height: 100%;
    display: block;
    object-fit: cover;
  }

  .upload-tip {
    margin-top: 8px;
    font-size: 12px;
    color: var(--el-text-color-secondary);
  }
}
</style> 