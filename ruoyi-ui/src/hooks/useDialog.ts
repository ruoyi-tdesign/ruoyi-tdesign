interface Options {
  title?: string;
}
export default (ops?: Options) => {
  const visible = ref(false);
  const title = ref(ops.title || '');

  const openDialog = () => {
    visible.value = true;
  };

  const closeDialog = () => {
    visible.value = false;
  };

  return {
    title,
    visible,

    openDialog,
    closeDialog,
  };
};
