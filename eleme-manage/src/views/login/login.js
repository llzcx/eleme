
import { isvalidUsername } from '@/utils/validate'
import LangSelect from '@/components/LangSelect'
export default {
  name: 'login',
  components: { LangSelect },
  data() {
    const validateUsername = (rule, value, callback) => {
      if (!isvalidUsername(value)) {
        callback(new Error('Please enter the correct user name'))
      } else {
        callback()
      }
    }
    const validatePassword = (rule, value, callback) => {
      if (value.length < 5) {
        callback(new Error('The password can not be less than 5 digits'))
      } else {
        callback()
      }
    }
    return {

      loginForm: {
        userType:'1',
        username: '',
        password: ''
      },
      loginRules: {
        username: [{ required: true, trigger: 'blur', validator: validateUsername }],
        password: [{ required: true, trigger: 'blur', validator: validatePassword }]
      },
      loading: false,
      pwdType: 'password'
    }
  },
  methods: {
    showPwd() {
      if (this.pwdType === 'password') {
        this.pwdType = ''
      } else {
        this.pwdType = 'password'
      }
    },
    handleLogin() {

      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          this.$store.dispatch('Login', this.loginForm).then(response => {
            console.log("return value:",response.data.success)
            this.loading = false
            if(response.data.success){
              console.log("登录成功")
              this.$router.push({ path: '/' })
            }else if(response.data.errorCode===2016){
              this.$message({
                message: response.data.errorMsg,
                type: 'error'
              })
            }else{
              this.$message({
                message: response.data.errorMsg,
                type: 'error'
              })
            }

          }).catch((err) => {
            console.log("登录失败",err)
            this.$message({
              message: "登录失败",
              type: 'error'
            })
            this.loading = false
          })
        } else {
          return false
        }
      })
    },
    gotoRegister(){
      this.$router.push("/register")
    }
  }
}
