import { createProdMockServer } from 'vite-plugin-mock/es/createProdMockServer'
// import { createProdMockServer } from 'vite-plugin-mock'
import userMock from './modules/user'
import roleMock from './modules/role'
import permissionMock from './modules/permission'
// import logMock from './modules/log'

export function setupProdMockServer() {
  createProdMockServer([
    ...userMock,
    ...roleMock,
    ...permissionMock,
  //  ...logMock
  ])
} 