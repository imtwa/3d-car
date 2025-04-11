<template>
    <div class="box" ref="boxRef" style="overflow: hidden"></div>
</template>
<script setup>
import * as THREE from 'three'
import TWEEN from '@tweenjs/tween.js'
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls'
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader'
import { GUI } from 'three/examples/jsm/libs/dat.gui.module'
import { RGBELoader } from 'three/examples/jsm/loaders/RGBELoader.js'
import { queryAttachmentInfoByIds } from '@/api/AttachmentStorage'
import { ref, reactive, onMounted } from 'vue'
import { ElLoading } from 'element-plus'

const props = defineProps({
    modelId: {
        type: [String, Number],
        required: true
    }
})

const modelUrl = ref('')
const boxRef = ref(null) // 创建一个ref来引用DOM元素
const skyTextures = reactive(['/sky1.hdr', '/sky2.hdr', '/sky3.hdr', '/sky4.hdr'])
const skyIndex = ref(0)

// 获取模型URL
const loadModelUrl = async () => {
    const loadingInstance = ElLoading.service({
        target: boxRef.value,
        text: '加载模型中...',
        background: 'rgba(0, 0, 0, 0.7)'
    })

    try {
        // console.log(props.modelId)

        const response = await queryAttachmentInfoByIds([props.modelId])
        if (response && response.data && response.data.length > 0) {
            modelUrl.value = '/api' + response.data[0].path
        }
    } catch (error) {
        console.error('获取模型URL失败:', error)
    } finally {
        loadingInstance.close()
    }
}

const getThreeJs = async () => {
    await loadModelUrl()
    if (!modelUrl.value) {
        console.error('未能获取到模型URL')
        return
    }

    // 创建场景
    const scene = new THREE.Scene()

    // 创建相机
    const camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 100)
    camera.position.set(0, 1, 5) // 设置相机位置

    // 创建渲染器
    const renderer = new THREE.WebGLRenderer({
        antialias: true //抗锯齿效果
    })
    renderer.setClearColor('#428bca', 1) //设置背景颜色
    renderer.setSize(window.innerWidth, window.innerHeight)
    renderer.setPixelRatio(window.devicePixelRatio)
    boxRef.value.appendChild(renderer.domElement)

    // 添加辅助坐标
    //   const axesHelper = new THREE.AxesHelper(5)
    //   scene.add(axesHelper)

    //轨道环绕
    const controls = new OrbitControls(camera, renderer.domElement)
    controls.enableDamping = true //惯性效果
    // OrbitControls 不需要被添加到场景中，它只需要在动画循环中被更新即可
    // scene.add(controls)

    //绘制地面网格
    const grid = new THREE.GridHelper(20, 40, 'red', 0xffffff)
    grid.material.opacity = 0.2
    grid.material.transparent = true
    scene.add(grid)

    /* ===== 需要修改的部分 ===== */
    //灯光
    const ambient = new THREE.AmbientLight(0xffffff, 1)
    ambient.position.set(0, 300, 0)
    scene.add(ambient)
    const directionalLight1 = new THREE.DirectionalLight(0xffffff, 0.5)
    directionalLight1.position.set(1, 0.7, 1)
    scene.add(directionalLight1)
    const directionalLight2 = new THREE.DirectionalLight(0xffffff, 0.5)
    directionalLight2.position.set(-1, 0.7, 1)
    scene.add(directionalLight2)
    const spotLight1 = new THREE.SpotLight(0xffffff, 1)
    spotLight1.position.set(0, 30, 0)
    scene.add(spotLight1)
    const spotLight2 = new THREE.SpotLight(0xffffff, 0.5)
    spotLight2.position.set(0, 20, -50)
    scene.add(spotLight2)
    // const spotLightHelper = new THREE.SpotLightHelper(spotLight1, 0xffffff)
    // scene.add(spotLightHelper);

    /* ===== 需要修改的部分 ===== */
    //车身材质
    const bodyMaterial = new THREE.MeshPhysicalMaterial({
        color: 0x65696d,
        metalness: 0.8,//金属感
        roughness: 0.5,//粗糙度
        clearcoat: 1.0,//喷漆
        clearcoatRoughness: 0.03//喷漆粗糙度
    })

    //玻璃材质
    const glassMaterial = new THREE.MeshPhysicalMaterial({
        color: 0xffffff,
        metalness: 0.25,
        roughness: 0,
        transmission: 1.0 //透光性 可以让很薄的透明表面，例如玻璃，变得更真实一些
    })

    //碳纤维材质
    const carbonMaterial = new THREE.MeshPhysicalMaterial({
        color: 'black',
        metalness: 0.3,
        roughness: 0.5,
        clearcoat: 1.0,
        clearcoatRoughness: 0.03
    })

    //铬材质
    const chromeMaterial = new THREE.MeshPhysicalMaterial({
        color: '#c0c0c0',
        metalness: 1.0,
        roughness: 0.2,
        clearcoat: 1.0,
        clearcoatRoughness: 0
    })

    /* ===== 需要修改的部分 ===== */
    //加载模型
    let leftdoors = []
    let rightdoors = []
    const modelLoadingInstance = ElLoading.service({
        target: boxRef.value,
        text: '加载模型中...',
        background: 'rgba(0, 0, 0, 0.7)'
    })

    new GLTFLoader().load(modelUrl.value, function (gltf) {
        const carModel = gltf.scene
        carModel.position.set(0, 0, 0)
        carModel.scale.set(1, 1, 1)
        carModel.traverse(function (obj) {
            if (obj.isMesh) {//判断是否是网格模型
                //材质双面
                obj.material.side = THREE.DoubleSide
                obj.material.emissiveIntensity = 1;
                obj.material.emissiveMap = obj.material.map;
            }
        });
        carModel.traverse((obj) => {//遍历模型树结构，查询模型节点
            // console.log(obj.name)
            if (obj.name == '网格'
                || obj.name == '网格013' || obj.name == '网格007' || obj.name == '网格003'
                || obj.name == '网格014' || obj.name == '网格011' || obj.name == '网格004' || obj.name == '网格001'
                || obj.name == '网格023' || obj.name == '网格019' || obj.name == '网格017' || obj.name == '网格022') {
                obj.material = bodyMaterial
            }
            else if (obj.name == '网格_2') {
                obj.material = carbonMaterial
            }
            else if (obj.name == '网格_4' || obj.name == '网格003_2' || obj.name == '网格004_4'
                || obj.name == '网格013_3' || obj.name == '网格007_3'
                || obj.name == '网格014_3' || obj.name == '网格011_3'
                || obj.name == '网格023_1' || obj.name == '网格019_1'
                || obj.name == '网格017_1' || obj.name == '网格022_1') {
                obj.material = chromeMaterial
            }
            else if (obj.name == 'DoorFrLeft' || obj.name == 'DoorRearLeft') {
                leftdoors.push(obj)
                console.log(leftdoors)
            }
            else if (obj.name == 'DoorFrRight' || obj.name == 'DoorRearRight') {
                rightdoors.push(obj)
                console.log(rightdoors)
            }
        })
        scene.add(carModel)
        modelLoadingInstance.close()
    })

    const obj = {
        color: 0x65696d,
        carOpen,
        carClose,
        carFirstIn,
        carSecondIn,
        carOut,
        skyChange
    }
    const gui = new GUI()
    gui
        .addColor(obj, 'color')
        .name('车身颜色')
        .onChange(value => {
            bodyMaterial.color.set(value)
        })
    gui.add(obj, 'carOpen').name('打开车门')
    gui.add(obj, 'carClose').name('关闭车门')
    gui.add(obj, 'carFirstIn').name('车内第一视角')
    gui.add(obj, 'carSecondIn').name('车内第二视角')
    gui.add(obj, 'carOut').name('车外视角')
    gui.add(obj, 'skyChange').name('切换背景')

    /* ===== 需要修改的部分 ===== */
    //打开车门
    function carOpen() {
        for (let index = 0; index < leftdoors.length; index++) {
            setAnimationDoor({ z: Math.PI / 2 }, { z: Math.PI / 4 }, leftdoors[index])
        }
        for (let index = 0; index < rightdoors.length; index++) {
            setAnimationDoor({ z: Math.PI / 2 }, { z: Math.PI / 4 }, rightdoors[index])
        }
    }
    //关闭车门
    function carClose() {
        for (let index = 0; index < leftdoors.length; index++) {
            setAnimationDoor({ z: Math.PI / 4 }, { z: Math.PI / 2 }, leftdoors[index])
        }
        for (let index = 0; index < rightdoors.length; index++) {
            setAnimationDoor({ z: Math.PI / 4 }, { z: Math.PI / 2 }, rightdoors[index])
        }
    }
    function setAnimationDoor(start, end, mesh) {
        const tween = new TWEEN.Tween(start).to(end, 1000).easing(TWEEN.Easing.Quadratic.Out)
        tween.onUpdate((that) => {
            mesh.rotation.z = that.z
        })
        tween.start()
    }
    //车内第一视角
    function carFirstIn() {
        setAnimationCamera({ cx: 2.25, cy: 1.4, cz: 4, ox: 0, oy: 0.5, oz: 0 }, { cx: 0, cy: 1, cz: -0.1, ox: 0, oy: 0.5, oz: 3 });
    }
    //车内第二视角
    function carSecondIn() {
        setAnimationCamera({ cx: 2.25, cy: 1.4, cz: 4, ox: 0, oy: 0.5, oz: 0 }, { cx: 0, cy: 1.2, cz: -1.1, ox: 0, oy: 0.5, oz: 3 });
    }
    //车外视角
    function carOut() {
        setAnimationCamera({ cx: 0, cy: 1, cz: -0.1, ox: 0, oy: 0.5, oz: 3 }, { cx: 2.25, cy: 1.4, cz: 4, ox: 0, oy: 0.5, oz: 0 });
    }
    function setAnimationCamera(start, end) {
        const tween = new TWEEN.Tween(start).to(end, 3000).easing(TWEEN.Easing.Quadratic.Out)
        tween.onUpdate((that) => {
            camera.position.set(that.cx, that.cy, that.cz)
            controls.target.set(that.ox, that.oy, that.oz)
        })
        tween.start()
    }

    /* ===== 不需要动 ===== */
    // 天空球
    const rgbeLoader = new RGBELoader()
    rgbeLoader.load(
        skyTextures[skyIndex.value],
        function (envMap) {
            scene.environment = envMap
            envMap.mapping = THREE.EquirectangularReflectionMapping
            scene.background = envMap
        },
        undefined,
        function (error) {
            console.error('加载天空球纹理失败:', error)
        }
    )

    //切换天空球
    function skyChange() {
        skyIndex.value = (skyIndex.value + 1) % skyTextures.length
        const skyLoadingInstance = ElLoading.service({
            target: boxRef.value,
            text: '切换背景中...',
            background: 'rgba(0, 0, 0, 0.7)'
        })

        rgbeLoader.load(
            skyTextures[skyIndex.value],
            function (envMap) {
                scene.environment = envMap
                envMap.mapping = THREE.EquirectangularReflectionMapping
                scene.background = envMap
                skyLoadingInstance.close()
            },
            undefined,
            function (error) {
                console.error('加载天空球纹理失败:', error)
                skyLoadingInstance.close()
            }
        )
    }

    // 创建一个时钟对象
    const clock = new THREE.Clock()
    // 声明渲染函数
    function animate(time) {
        requestAnimationFrame(animate)
        const delta = clock.getDelta() // 使用clock来获取帧间隔时间
        if (delta < 0.1) {
            // 如果帧间隔时间小于0.1秒，则进行渲染
            renderer.render(scene, camera)
            TWEEN.update(time)
            controls.update()
        }
    }
    animate()
}

onMounted(() => {
    getThreeJs()
})
</script>
<style scoped></style>